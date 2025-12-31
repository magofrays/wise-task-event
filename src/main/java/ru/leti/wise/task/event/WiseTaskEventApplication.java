package ru.leti.wise.task.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import ru.leti.wise.task.event.configuration.StatisticsProperties;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(StatisticsProperties.class)
public class WiseTaskEventApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiseTaskEventApplication.class, args);
    }

}
