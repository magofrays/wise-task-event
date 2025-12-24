package ru.leti.wise.task.event.entity;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.UUID;

@Value
@Builder
public class Statistic {
    UUID id;
    StatisticScope scope;
    Timestamp updated_at;
    StatisticType type;
    Double value;
    UUID sessionId;
    UUID taskId;
    UUID userId;
    String eventType;
}
