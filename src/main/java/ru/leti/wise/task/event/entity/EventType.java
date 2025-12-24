package ru.leti.wise.task.event.entity;


import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class EventType {
    Integer id;
    String event_name;
}
