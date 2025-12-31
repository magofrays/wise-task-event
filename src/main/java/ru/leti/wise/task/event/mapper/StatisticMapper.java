package ru.leti.wise.task.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leti.wise.task.event.Statistic;
import ru.leti.wise.task.event.entity.Session;
import ru.leti.wise.task.event.entity.StatisticEntity;
import ru.leti.wise.task.event.exception.BusinessException;
import ru.leti.wise.task.event.exception.ErrorCode;
import ru.leti.wise.task.event.repository.SessionRepository;

@Component
@RequiredArgsConstructor
public class StatisticMapper {
    public Statistic.StatisticResponse toResponse(StatisticEntity statistic) {
        if (statistic == null) {
            return null;
        }
        var builder = Statistic.StatisticResponse.newBuilder();
        builder.setEventType(statistic.getEventType());
        builder.setType(statistic.getType());
        builder.setScope(statistic.getScope());
        com.google.protobuf.Timestamp protoTimestamp =
                com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(statistic.getUpdatedAt().getEpochSecond())
                        .setNanos(statistic.getUpdatedAt().getNano())
                        .build();
        builder.setUpdateAt(protoTimestamp);
        builder.setValue(statistic.getValue());

        if (statistic.getUserId() != null) {
            builder.setUserId(statistic.getUserId().toString());
        }
        if (statistic.getTaskId() != null) {
            builder.setTaskId(statistic.getTaskId().toString());
        }
        return builder.build();
    }

}
