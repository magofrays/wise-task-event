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



//    @Cacheable(value = "statistics", key = "{#request.type, #request.scope, #request.eventType, #request.taskId, #request.userId}")
    public Statistic.StatisticResponse getCachedStatistic(Statistic.StatisticRequest request){
        switch (request.getType()){
            case SUM -> {
                return sumOperation.execute(request, true);
            }
            case MEAN -> {
                return meanOperation.execute(request, true);
            }
            case SUCCESS_RATE -> {
                return successRateOperation.execute(request, true);
            }
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "No such statistics type: " + request.getType() + "!");
        }
    }




}
