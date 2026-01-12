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

    public Statistic.StatisticResponse execute(Statistic.StatisticRequest request, Boolean cache) {
        return Statistic.StatisticResponse.newBuilder().build();
    }

}

