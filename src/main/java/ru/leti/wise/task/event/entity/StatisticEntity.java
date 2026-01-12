package ru.leti.wise.task.event.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.leti.wise.task.event.Statistic;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Table(name = "statistic")
public class StatisticEntity implements Serializable {
    @Id
    private UUID id;
    private Statistic.StatisticScope scope;
    private Instant updatedAt;
    private Statistic.StatisticType type;
    private Float value;
    @Column("task_id")
    private UUID taskId;
    @Column("user_id")
    private UUID userId;
    @Column("event_type")
    private String eventType;
}
