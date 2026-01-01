package ru.leti.wise.task.event.service.grpc.operations;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.dto.TaskUserCount;
import ru.leti.wise.task.event.repository.EventRepository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetSumOperation {
    private final EventRepository eventRepository;

    public Double execute(Statistic.StatisticRequest request, Instant from) {
        UUID taskId = request.hasTaskId() ? UUID.fromString(request.getTaskId()) : null;
        UUID userId = request.hasUserId() ? UUID.fromString(request.getUserId()) : null;
        var result = eventRepository.getEventCount(request.getEventType(), from, taskId, userId).collectList().block();
        return extractTotalSum(result);
    }

    public static Double extractTotalSum(List<TaskUserCount> data) {
        Double sum = 0.0;
        if(data == null){
            return sum;
        }
        for(var tuc : data){
            sum += tuc.getCount();
        }
        return sum;
    }
}
