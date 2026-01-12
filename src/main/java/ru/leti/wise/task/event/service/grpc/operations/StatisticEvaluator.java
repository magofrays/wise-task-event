package ru.leti.wise.task.event.service.grpc.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.entity.StatisticEntity;
import ru.leti.wise.task.event.exception.BusinessException;
import ru.leti.wise.task.event.exception.ErrorCode;
import ru.leti.wise.task.event.mapper.StatisticMapper;
import ru.leti.wise.task.event.repository.EventTypeRepository;
import ru.leti.wise.task.event.repository.StatisticRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StatisticEvaluator {
    private final StatisticRepository statisticRepository;
    private final EventTypeRepository eventTypeRepository;

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

    public void validateEventType(Statistic.StatisticRequest request){
        if(request.getEventType().isEmpty()){
            throw new BusinessException(ErrorCode.NOT_FOUND, "Empty event type!");
        }
        if(eventTypeRepository.findByEventName(request.getEventType()).block() == null){
            throw new BusinessException(ErrorCode.NOT_FOUND, "No such event type name: " + request.getEventType() + "!");
        }
    }
}
