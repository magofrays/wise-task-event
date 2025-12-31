package ru.leti.wise.task.event.service.operations;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.dto.TaskUserCount;
import ru.leti.wise.task.event.repository.EventRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import java.util.UUID;

import static ru.leti.wise.task.event.service.operations.GetSumOperation.extractTotalSum;

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
        return sum/data.size();
    }
}

