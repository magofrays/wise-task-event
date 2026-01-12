package ru.leti.wise.task.event.service.grpc.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.configuration.StatisticsProperties;
import ru.leti.wise.task.event.entity.StatisticEntity;
import ru.leti.wise.task.event.mapper.StatisticMapper;
import ru.leti.wise.task.event.repository.EventRepository;
import ru.leti.wise.task.event.repository.StatisticRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;




@Component
@RequiredArgsConstructor
public class GetSuccessRateOperation {
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final GetSumOperation getSumOperation;
    private final StatisticEvaluator statisticEvaluator;
    private final StatisticsProperties statisticsProperties;


    public Statistic.StatisticResponse execute(Statistic.StatisticRequest request, Boolean cached) {
        var statisticEntity = statisticEvaluator
                .findStatistic(request)
                .orElse(
                        StatisticEntity
                                .builder()
                                .type(request.getType())
                                .scope(request.getScope())
                                .value(0.0f)
                                .taskId(request.hasTaskId() ? UUID.fromString(request.getTaskId()) : null)
                                .userId(request.hasUserId() ? UUID.fromString(request.getUserId()) : null)
                                .eventType("task_success,task_wrong")
                                .build()
                );
        if(cached){
            if(statisticEntity.getUpdatedAt() != null){
                Duration diff = Duration.between(statisticEntity.getUpdatedAt(), Instant.now());
                if(diff.getSeconds() <= statisticsProperties.getTimeToLive()) {
                    return statisticMapper.toResponse(statisticEntity);
                }
            }
        }
        var successRequest = Statistic.StatisticRequest.newBuilder()
                .setScope(statisticEntity.getScope())
                .setType(Statistic.StatisticType.SUM)
                .setEventType("task_success");
        var wrongRequest = Statistic.StatisticRequest.newBuilder()
                .setScope(statisticEntity.getScope())
                .setType(Statistic.StatisticType.SUM)
                .setEventType("task_wrong");
        if(request.hasUserId()){
            successRequest.setUserId(request.getUserId());
            wrongRequest.setUserId(request.getUserId());
        }
        if(request.hasTaskId()){
            successRequest.setTaskId(request.getTaskId());
            wrongRequest.setTaskId(request.getTaskId());
        }
        var successData = getSumOperation.execute(successRequest.build(), false);
        var wrongData = getSumOperation.execute(wrongRequest.build(), false);
        statisticEntity.setValue(successData.getValue() == 0 ? 0 : 100 * successData.getValue() / (successData.getValue() + wrongData.getValue()));
        statisticEntity.setUpdatedAt(Instant.now());
        var response = statisticMapper.toResponse(statisticRepository.save(statisticEntity).block());
        return response;
    }

}

