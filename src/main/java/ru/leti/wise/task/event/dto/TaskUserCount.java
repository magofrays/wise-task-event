package ru.leti.wise.task.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TaskUserCount {
    private UUID taskId;
    private UUID userId;
    private Long count;
}