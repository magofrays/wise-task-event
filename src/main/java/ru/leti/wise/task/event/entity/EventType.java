package ru.leti.wise.task.event.entity;


import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Value
@Builder
@Table("event_type")
public class EventType {
    @Id
    Integer id;
    @Column("event_name")
    String eventName;
}
