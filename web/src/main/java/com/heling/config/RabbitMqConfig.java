package com.heling.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author whl
 * @description
 * @date 2019/08/11 21:42
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue testQueue() {
        return new Queue("TEST_QUEUE");
    }

}
