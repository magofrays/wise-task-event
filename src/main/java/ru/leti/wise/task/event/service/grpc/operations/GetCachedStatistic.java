package ru.leti.wise.task.event.service.grpc.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.configuration.StatisticsProperties;
import ru.leti.wise.task.event.entity.StatisticEntity;
import ru.leti.wise.task.event.exception.BusinessException;
import ru.leti.wise.task.event.exception.ErrorCode;
import ru.leti.wise.task.event.mapper.StatisticMapper;
import ru.leti.wise.task.event.repository.EventTypeRepository;
import ru.leti.wise.task.event.repository.StatisticRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetCachedStatistic {
    private final StatisticsProperties statisticsProperties;
    private final GetSuccessRateOperation successRateOperation;
    private final GetSumOperation sumOperation;
    private final GetMeanOperation meanOperation;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final EventTypeRepository eventTypeRepository;

    @Cacheable(value = "statistics", key = "{#request.type, #request.scope, #request.eventType, #request.taskId, #request.userId}")
    public Statistic.StatisticResponse getCachedStatistic(Statistic.StatisticRequest request){
        var statisticEntity = findStatistic(request);
        return statisticEntity.map(entity -> {
            Duration diff = Duration.between(entity.getUpdatedAt(), Instant.now());
            if(diff.getSeconds() > statisticsProperties.getTimeToLive()) {
                calculateStatistics(request, entity, entity.getUpdatedAt());
                entity.setUpdatedAt(Instant.now());
                statisticRepository.save(entity).block();
            }
            return statisticMapper.toResponse(entity);
        }).orElseGet( () -> {
            var entity = StatisticEntity.builder().build();
            entity.setScope(request.getScope());
            entity.setType(request.getType());
            calculateStatistics(request, entity, null);
            entity.setUpdatedAt(Instant.now());
            if(request.hasTaskId()){
                entity.setTaskId(UUID.fromString(request.getTaskId()));
            }
            if(request.hasUserId()){
                entity.setUserId(UUID.fromString(request.getUserId()));
            }
            entity = statisticRepository.save(entity).block();
            return statisticMapper.toResponse(entity);
        });
    }

    public void calculateStatistics(Statistic.StatisticRequest request, StatisticEntity entity, Instant from){
        switch (request.getType()){
            case SUM -> {
                entity.setValue(sumOperation.execute(request, from));
                if(entity.getEventType() == null){
                    validateEventType(request);
                    entity.setEventType(request.getEventType());
                }
            }
            case MEAN -> {
                entity.setValue(meanOperation.execute(request, from));
                if(entity.getEventType() == null){
                    validateEventType(request);
                    entity.setEventType(request.getEventType());
                }
            }
            case SUCCESS_RATE -> {
                entity.setValue(successRateOperation.execute(request, from));
                if(entity.getEventType() == null){
                    entity.setEventType("task_success,task_wrong");
                }
            }
            default -> {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "No such statistics type: " + request.getType() + "!");
            }
        }

    }

    private void validateEventType(Statistic.StatisticRequest request){
        if(request.getEventType().isEmpty()){
            throw new BusinessException(ErrorCode.NOT_FOUND, "Empty event type!");
        }
        if(eventTypeRepository.findByEventName(request.getEventType()).block() == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "No such event type name: " + request.getEventType() + "!");
        }
    }

    public Optional<StatisticEntity> findStatistic(Statistic.StatisticRequest request){
        UUID taskId = request.hasTaskId() ? UUID.fromString(request.getTaskId()) : null;
        UUID userId = request.hasUserId() ? UUID.fromString(request.getUserId()) : null;
        switch (request.getScope()){
            case TASK -> {
                if(taskId == null){
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "There is must be taskId in request!");
                }
                return Optional.ofNullable(
                        statisticRepository.findByTaskIdAndTypeAndScopeAndEventType(
                                taskId,
                                request.getType(),
                                request.getScope(),
                                request.getEventType()
                        ).block());
            }
            case USER -> {
                if(userId == null){
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "There is must be userId in request!");
                }
                return Optional.ofNullable(
                        statisticRepository.findByUserIdAndTypeAndScopeAndEventType(
                                userId,
                                request.getType(),
                                request.getScope(),
                                request.getEventType()
                        ).block());
            }
            case SESSION -> {
                if(userId == null){
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "There are must be taskId and userId in request!");
                }
                return Optional.ofNullable(
                        statisticRepository.findByTaskIdAndUserIdAndTypeAndScopeAndEventType(
                                taskId,
                                userId,
                                request.getType(),
                                request.getScope(),
                                request.getEventType()
                        ).block());
            }
            case GLOBAL -> {
                return Optional.ofNullable(
                        statisticRepository.findByTypeAndScopeAndEventType(
                                request.getType(),
                                request.getScope(),
                                request.getEventType()
                        ).block());
            }
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "There is no such scope!");
    }
}
