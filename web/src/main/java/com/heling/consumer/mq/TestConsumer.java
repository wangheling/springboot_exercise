package com.heling.consumer.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author whl
 * @description
 * @date 2019/08/11 21:44
 */
@Slf4j
@Component
@RabbitListener(queues = "TEST_QUEUE")
public class TestConsumer {

    @RabbitHandler
    public void process(String message) {
        log.info("收到消息:" + message);
    }

}
