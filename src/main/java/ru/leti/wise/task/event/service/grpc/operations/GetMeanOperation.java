package ru.leti.wise.task.event.service.grpc.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.dto.TaskUserCount;
import ru.leti.wise.task.event.repository.EventRepository;

import java.time.Instant;
import java.util.List;

import java.util.UUID;

import static ru.leti.wise.task.event.service.grpc.operations.GetSumOperation.extractTotalSum;

@Component
@RequiredArgsConstructor
public class GetMeanOperation {
    private final EventRepository eventRepository;

    public Double execute(Statistic.StatisticRequest request, Instant from) {
        UUID taskId = request.hasTaskId() ? UUID.fromString(request.getTaskId()) : null;
        UUID userId = request.hasUserId() ? UUID.fromString(request.getUserId()) : null;
        var result = eventRepository.getEventCount(request.getEventType(), from, taskId, userId).collectList().block();
        return getMean(result);
    }

    public static Double getMean(List<TaskUserCount> data) {
        Double sum = extractTotalSum(data);
        if(sum == 0){
            return 0.0;
        }
        return sum/data.size();
    }
}

