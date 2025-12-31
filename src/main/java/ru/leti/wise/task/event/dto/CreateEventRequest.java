package ru.leti.wise.task.event.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateEventRequest {
    private UUID userId;
    @NotNull(message="There is must be taskId")
    private UUID taskId;
    @NotNull(message="There is must be eventType")
    private String eventType;
    private Integer eventEntityId;
    @NotNull(message="There is must be eventValue")
    private String eventValue;
}
