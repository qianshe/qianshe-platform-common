package com.qianshe.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 
 * 配置通知服务使用的消息队列
 * 支持多种通知渠道：邮件、短信、站内信、推送、微信
 *
 * @author qianshe
 * @since 1.0.0
 */
@Configuration
public class RabbitConfig {

    /**
     * 通知交换机名称
     */
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";

    /**
     * 邮件通知队列名称
     */
    public static final String EMAIL_QUEUE = "notification.email.queue";

    /**
     * 短信通知队列名称
     */
    public static final String SMS_QUEUE = "notification.sms.queue";

    /**
     * 站内信通知队列名称
     */
    public static final String INAPP_QUEUE = "notification.inapp.queue";

    /**
     * 推送通知队列名称
     */
    public static final String PUSH_QUEUE = "notification.push.queue";

    /**
     * 微信通知队列名称
     */
    public static final String WECHAT_QUEUE = "notification.wechat.queue";

    /**
     * 路由键：邮件通知
     */
    public static final String EMAIL_ROUTING_KEY = "notification.email";

    /**
     * 路由键：短信通知
     */
    public static final String SMS_ROUTING_KEY = "notification.sms";

    /**
     * 路由键：站内信通知
     */
    public static final String INAPP_ROUTING_KEY = "notification.inapp";

    /**
     * 路由键：推送通知
     */
    public static final String PUSH_ROUTING_KEY = "notification.push";

    /**
     * 路由键：微信通知
     */
    public static final String WECHAT_ROUTING_KEY = "notification.wechat";

    /**
     * 配置JSON消息转换器
     * 用于自动将Java对象转换为JSON，以及将JSON转换为Java对象
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建通知交换机
     * 使用Topic类型，支持灵活的路由规则
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    /**
     * 创建邮件通知队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    /**
     * 创建短信通知队列
     */
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE).build();
    }

    /**
     * 创建站内信通知队列
     */
    @Bean
    public Queue inappQueue() {
        return QueueBuilder.durable(INAPP_QUEUE).build();
    }

    /**
     * 创建推送通知队列
     */
    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(PUSH_QUEUE).build();
    }

    /**
     * 创建微信通知队列
     */
    @Bean
    public Queue wechatQueue() {
        return QueueBuilder.durable(WECHAT_QUEUE).build();
    }

    /**
     * 绑定邮件通知队列到交换机
     */
    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(notificationExchange())
                .with(EMAIL_ROUTING_KEY);
    }

    /**
     * 绑定短信通知队列到交换机
     */
    @Bean
    public Binding smsBinding() {
        return BindingBuilder
                .bind(smsQueue())
                .to(notificationExchange())
                .with(SMS_ROUTING_KEY);
    }

    /**
     * 绑定站内信通知队列到交换机
     */
    @Bean
    public Binding inappBinding() {
        return BindingBuilder
                .bind(inappQueue())
                .to(notificationExchange())
                .with(INAPP_ROUTING_KEY);
    }

    /**
     * 绑定推送通知队列到交换机
     */
    @Bean
    public Binding pushBinding() {
        return BindingBuilder
                .bind(pushQueue())
                .to(notificationExchange())
                .with(PUSH_ROUTING_KEY);
    }

    /**
     * 绑定微信通知队列到交换机
     */
    @Bean
    public Binding wechatBinding() {
        return BindingBuilder
                .bind(wechatQueue())
                .to(notificationExchange())
                .with(WECHAT_ROUTING_KEY);
    }
}

