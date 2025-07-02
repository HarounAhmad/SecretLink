package io.erisdev.secretlink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cleanup.task")
public class SchedulerConfig {
    private long rate = 3600000; // default 1 hour in ms
}