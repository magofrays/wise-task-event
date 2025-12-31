package ru.leti.wise.task.event.service.operations;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.mapper.StatisticMapper;
import ru.leti.wise.task.event.repository.EventRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static ru.leti.wise.task.event.Statistic.*;
import static ru.leti.wise.task.event.service.operations.GetSumOperation.extractTotalSum;


@Component
@RequiredArgsConstructor
public class GetSuccessRateOperation {
    private final EventRepository eventRepository;

    public Double execute(StatisticRequest request, Instant from) {
        UUID taskId = request.hasTaskId() ? UUID.fromString(request.getTaskId()) : null;
        UUID userId = request.hasUserId() ? UUID.fromString(request.getUserId()) : null;
        var successData = eventRepository.getEventCount("task_success", from, taskId, userId).collectList().block();
        var wrongData = eventRepository.getEventCount("task_wrong", from, taskId, userId).collectList().block();
        double success = extractTotalSum(successData);
        double wrong = extractTotalSum(wrongData);
        return 100 * success / (success + wrong);
    }

}

