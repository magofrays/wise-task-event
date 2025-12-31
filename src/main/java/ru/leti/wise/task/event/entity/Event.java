package ru.leti.wise.task.event.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;


@Value
@AllArgsConstructor
@Builder
@Table("event")
public class Event {
    @Id
    UUID id;
    Instant time;
    @Column("session_id")
    UUID sessionId;
    @Column("event_type_id")
    Integer eventTypeId;
    @Column("event_entity_id")
    Integer eventEntityId;
    @Column("event_value")
    String eventValue;
}
