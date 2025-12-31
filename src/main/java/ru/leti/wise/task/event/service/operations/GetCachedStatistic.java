package ru.leti.wise.task.event.service.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.configuration.StatisticsProperties;
import ru.leti.wise.task.event.entity.StatisticEntity;
import ru.leti.wise.task.event.exception.BusinessException;
import ru.leti.wise.task.event.exception.ErrorCode;
import ru.leti.wise.task.event.mapper.StatisticMapper;
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

    @Cacheable(value = "statistics", key = "{#request.type, #request.scope, #request.eventType, #request.taskId, #request.userId}")
    public Statistic.StatisticResponse getCachedStatistic(Statistic.StatisticRequest request){
        var statisticEntity = findStatistic(request);
        return statisticEntity.map(entity -> {
            Duration diff = Duration.between(entity.getUpdatedAt(), Instant.now());
            if(diff.getSeconds() > statisticsProperties.getTimeToLive()) {
                entity.setValue(
                        calculateStatistics(request, entity.getUpdatedAt())
                );
                entity.setUpdatedAt(Instant.now());
                statisticRepository.save(entity).block();
            }
            return statisticMapper.toResponse(entity);
        }).orElseGet( () -> {
            var statisticBuilder = StatisticEntity.builder();
            statisticBuilder.scope(request.getScope());
            statisticBuilder.type(request.getType());
            statisticBuilder.value(
                    calculateStatistics(request, null)
            );
            statisticBuilder.eventType(request.getEventType());
            statisticBuilder.updatedAt(Instant.now());
            if(request.hasTaskId()){
                statisticBuilder.taskId(UUID.fromString(request.getTaskId()));
            }
            if(request.hasUserId()){
                statisticBuilder.userId(UUID.fromString(request.getUserId()));
            }
            var entity = statisticRepository.save(statisticBuilder.build()).block();
            return statisticMapper.toResponse(entity);
        });
    }

    public Double calculateStatistics(Statistic.StatisticRequest request, Instant from){
        switch (request.getType()){
            case SUM -> {
                return sumOperation.execute(request, from);
            }
            case MEAN -> {
                return meanOperation.execute(request, from);
            }
            case SUCCESS_RATE -> {
                return successRateOperation.execute(request, from);
            }
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "No such statistics type: " + request.getType() + "!");
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
