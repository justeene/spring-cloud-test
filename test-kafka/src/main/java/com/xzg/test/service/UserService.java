package com.xzg.test.service;

import com.xzg.test.config.EsChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午4:10
 */
@Service
public class UserService {
    @Autowired
    private EsChannel channel;

    /**
     * 消息发送到默认通道：缺省通道对应缺省主题
     *
     * @param message
     */
    public void sendToDefaultChannel(String message) {
        channel.sendEsDefaultMessage().send(MessageBuilder.withPayload(message).build());
    }

    /**
     * 消息发送到告警通道：告警通道对应告警主题
     *
     * @param message
     */
    public void sendToAlarmChannel(String message) {
        channel.sendEsAlarmMessage().send(MessageBuilder.withPayload(message).build());
    }

    /**
     * 从缺省通道接收消息
     *
     * @param message
     */
    @StreamListener(EsChannel.ES_DEFAULT_INPUT)
    public void receive(Message<String> message) throws InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        System.out.println(sdf.format(new Date()) + "------start--------默认消息：" + message);
        Thread.sleep(1000);
//        System.out.println(sdf.format(new Date()) + "------end--------默认消息");
    }

    /**
     * 从告警通道接收消息
     *
     * @param message
     */
    @StreamListener(EsChannel.ES_ALARM_INPUT)
    public void receiveAlarm(Message<String> message) {
        System.out.println("订阅告警消息：" + message);
    }
}
