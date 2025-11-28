package org.wise.task.wise.task.event.filter;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class EventFilter {
    private Integer eventTypeId;
    private UUID taskId;
    private UUID userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String eventValue;
}
