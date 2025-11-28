package org.wise.task.wise.task.event.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.sql.Timestamp;
import java.util.UUID;

@Value
@Builder
@AllArgsConstructor
public class Session {
    UUID id;
    UUID user_id;
    UUID task_id;
    Timestamp created_at;
}
