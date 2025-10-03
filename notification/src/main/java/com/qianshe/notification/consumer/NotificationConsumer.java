package com.qianshe.notification.consumer;

import com.qianshe.notification.config.RabbitConfig;
import com.qianshe.notification.dto.NotificationMessage;
import com.qianshe.notification.dto.SendNotificationRequest;
import com.qianshe.notification.entity.Notification;
import com.qianshe.notification.service.NotificationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 通知消息消费者
 * 
 * 监听RabbitMQ队列，接收并处理通知消息
 * 支持多种通知渠道的异步处理
 *
 * @author qianshe
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    /**
     * 处理邮件通知消息
     *
     * @param notificationMessage 通知消息
     * @param message RabbitMQ原始消息
     * @param channel RabbitMQ通道
     */
    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void handleEmailNotification(NotificationMessage notificationMessage, 
                                       Message message, 
                                       Channel channel) {
        try {
            log.info("收到邮件通知请求，接收者ID: {}, 标题: {}", 
                    notificationMessage.getReceiverId(), 
                    notificationMessage.getTitle());

            // 转换为发送请求
            SendNotificationRequest request = convertToRequest(notificationMessage);

            // 发送通知
            Notification notification = notificationService.sendNotification(request);

            if (notification != null) {
                log.info("邮件通知发送成功，通知ID: {}, 接收者ID: {}", 
                        notification.getId(), 
                        notificationMessage.getReceiverId());
                
                // 手动确认消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("邮件通知发送失败，接收者ID: {}", notificationMessage.getReceiverId());
                // 拒绝消息并重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        } catch (Exception e) {
            log.error("处理邮件通知失败，接收者ID: {}, 错误: {}", 
                    notificationMessage.getReceiverId(), 
                    e.getMessage(), e);
            try {
                // 拒绝消息并重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }

    /**
     * 处理短信通知消息
     *
     * @param notificationMessage 通知消息
     * @param message RabbitMQ原始消息
     * @param channel RabbitMQ通道
     */
    @RabbitListener(queues = RabbitConfig.SMS_QUEUE)
    public void handleSmsNotification(NotificationMessage notificationMessage, 
                                     Message message, 
                                     Channel channel) {
        try {
            log.info("收到短信通知请求，接收者ID: {}, 手机号: {}", 
                    notificationMessage.getReceiverId(), 
                    notificationMessage.getPhone());

            SendNotificationRequest request = convertToRequest(notificationMessage);
            Notification notification = notificationService.sendNotification(request);

            if (notification != null) {
                log.info("短信通知发送成功，通知ID: {}, 接收者ID: {}", 
                        notification.getId(), 
                        notificationMessage.getReceiverId());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("短信通知发送失败，接收者ID: {}", notificationMessage.getReceiverId());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        } catch (Exception e) {
            log.error("处理短信通知失败，接收者ID: {}, 错误: {}", 
                    notificationMessage.getReceiverId(), 
                    e.getMessage(), e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }

    /**
     * 处理站内信通知消息
     *
     * @param notificationMessage 通知消息
     * @param message RabbitMQ原始消息
     * @param channel RabbitMQ通道
     */
    @RabbitListener(queues = RabbitConfig.INAPP_QUEUE)
    public void handleInappNotification(NotificationMessage notificationMessage, 
                                       Message message, 
                                       Channel channel) {
        try {
            log.info("收到站内信通知请求，接收者ID: {}, 标题: {}", 
                    notificationMessage.getReceiverId(), 
                    notificationMessage.getTitle());

            SendNotificationRequest request = convertToRequest(notificationMessage);
            Notification notification = notificationService.sendNotification(request);

            if (notification != null) {
                log.info("站内信通知发送成功，通知ID: {}, 接收者ID: {}", 
                        notification.getId(), 
                        notificationMessage.getReceiverId());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("站内信通知发送失败，接收者ID: {}", notificationMessage.getReceiverId());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        } catch (Exception e) {
            log.error("处理站内信通知失败，接收者ID: {}, 错误: {}", 
                    notificationMessage.getReceiverId(), 
                    e.getMessage(), e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }

    /**
     * 处理推送通知消息
     *
     * @param notificationMessage 通知消息
     * @param message RabbitMQ原始消息
     * @param channel RabbitMQ通道
     */
    @RabbitListener(queues = RabbitConfig.PUSH_QUEUE)
    public void handlePushNotification(NotificationMessage notificationMessage, 
                                      Message message, 
                                      Channel channel) {
        try {
            log.info("收到推送通知请求，接收者ID: {}, 标题: {}", 
                    notificationMessage.getReceiverId(), 
                    notificationMessage.getTitle());

            SendNotificationRequest request = convertToRequest(notificationMessage);
            Notification notification = notificationService.sendNotification(request);

            if (notification != null) {
                log.info("推送通知发送成功，通知ID: {}, 接收者ID: {}", 
                        notification.getId(), 
                        notificationMessage.getReceiverId());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("推送通知发送失败，接收者ID: {}", notificationMessage.getReceiverId());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        } catch (Exception e) {
            log.error("处理推送通知失败，接收者ID: {}, 错误: {}", 
                    notificationMessage.getReceiverId(), 
                    e.getMessage(), e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }

    /**
     * 处理微信通知消息
     *
     * @param notificationMessage 通知消息
     * @param message RabbitMQ原始消息
     * @param channel RabbitMQ通道
     */
    @RabbitListener(queues = RabbitConfig.WECHAT_QUEUE)
    public void handleWechatNotification(NotificationMessage notificationMessage, 
                                        Message message, 
                                        Channel channel) {
        try {
            log.info("收到微信通知请求，接收者ID: {}, 标题: {}", 
                    notificationMessage.getReceiverId(), 
                    notificationMessage.getTitle());

            SendNotificationRequest request = convertToRequest(notificationMessage);
            Notification notification = notificationService.sendNotification(request);

            if (notification != null) {
                log.info("微信通知发送成功，通知ID: {}, 接收者ID: {}", 
                        notification.getId(), 
                        notificationMessage.getReceiverId());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.error("微信通知发送失败，接收者ID: {}", notificationMessage.getReceiverId());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        } catch (Exception e) {
            log.error("处理微信通知失败，接收者ID: {}, 错误: {}", 
                    notificationMessage.getReceiverId(), 
                    e.getMessage(), e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }

    /**
     * 将NotificationMessage转换为SendNotificationRequest
     *
     * @param message 通知消息
     * @return 发送通知请求
     */
    private SendNotificationRequest convertToRequest(NotificationMessage message) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setType(message.getType());
        request.setChannel(message.getChannel());
        request.setReceiverId(message.getReceiverId());
        request.setSenderId(message.getSenderId());
        request.setTitle(message.getTitle());
        request.setContent(message.getContent());
        request.setTemplateId(message.getTemplateId());
        request.setTemplateParams(message.getTemplateParams());
        request.setBusinessId(message.getBusinessId());
        request.setBusinessType(message.getBusinessType());
        request.setExtraData(message.getExtraData());
        return request;
    }
}

