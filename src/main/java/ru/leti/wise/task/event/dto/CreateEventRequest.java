package ru.leti.wise.task.event.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class CreateEventRequest {
    private UUID userId;
    private UUID taskId;
    private Integer eventTypeId;
    private Integer eventEntityId;
    private String eventValue;
}
