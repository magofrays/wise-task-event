package org.wise.task.wise.task.event.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.UUID;


@Value
@AllArgsConstructor
@Builder
public class Event {
    UUID id;
    Timestamp time;
    UUID session_id;
    Integer event_type_id;
    Integer event_entity_id;
    String event_value;
}
