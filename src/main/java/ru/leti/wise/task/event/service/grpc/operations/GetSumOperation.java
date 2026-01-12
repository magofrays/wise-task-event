package ru.leti.wise.task.event.service.grpc.operations;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.configuration.StatisticsProperties;
import ru.leti.wise.task.event.dto.TaskUserCount;
import ru.leti.wise.task.event.entity.StatisticEntity;
import ru.leti.wise.task.event.mapper.StatisticMapper;
import ru.leti.wise.task.event.repository.EventRepository;
import ru.leti.wise.task.event.repository.StatisticRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetSumOperation {
    private final EventRepository eventRepository;
    private final StatisticEvaluator statisticEvaluator;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
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
        statisticEvaluator.validateEventType(request);
        statisticEntity.setEventType(request.getEventType());
        var result = eventRepository.getEventCount(request.getEventType(),
                statisticEntity.getUpdatedAt(),
                statisticEntity.getTaskId(),
                statisticEntity.getUserId()).collectList().block();
        statisticEntity.setValue(statisticEntity.getValue() + extractTotalSum(result));
        statisticEntity.setUpdatedAt(Instant.now());
        return statisticMapper.toResponse(
                statisticRepository.save(statisticEntity).block()
        );
    }

    public static Float extractTotalSum(List<TaskUserCount> data) {
        Float sum = 0.0f;
        if(data == null){
            return sum;
        }
        for(var tuc : data){
            sum += tuc.getCount();
        }
        return sum;
    }
}
