package ru.leti.wise.task.event.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Timestamp;
import java.util.UUID;

@Value
@Builder
@AllArgsConstructor
public class Session {
    @Id
    UUID id;
    @Column("user_id")
    UUID userId;
    @Column("task_id")
    UUID taskId;
    @Column("created_at")
    Timestamp createdAt;
}
