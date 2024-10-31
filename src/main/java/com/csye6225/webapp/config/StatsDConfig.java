package com.csye6225.webapp.config;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsDConfig {

    @Value("${publish.metrics}")
    private boolean publishMessage;

    @Value("${metrics.server.hostname}")
    private String metricHost;

    @Value("${metrics.server.port}")
    private int portNumber;

    @Value("${metrics.server.prefix}")
    private String prefix;

    @Bean
    public StatsDClient metricClient() {
        if (publishMessage) {
            return new NonBlockingStatsDClient(prefix, metricHost, portNumber);
        }
        return new NoOpStatsDClient();
    }
}
