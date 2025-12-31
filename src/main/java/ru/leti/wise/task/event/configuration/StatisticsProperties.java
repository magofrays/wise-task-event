package ru.leti.wise.task.event.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wise-task-event.statistics")
public class StatisticsProperties {
    private Integer timeToLive;
}
