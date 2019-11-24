package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka 相关的服务借口定义
 */
public interface IKafkaService {

    /**
     * 消费优惠券 Kafka 消息I
     * @param record
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
