package com.xzg.test.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-9下午4:40
 */
public interface EsChannel {
    /**
     * 缺省发送消息通道名称
     */
    String ES_DEFAULT_OUTPUT = "es_default_output";

    /**
     * 缺省接收消息通道名称
     */
    String ES_DEFAULT_INPUT = "es_default_input";

    /**
     * 告警发送消息通道名称
     */
    String ES_ALARM_OUTPUT = "es_alarm_output";

    /**
     * 告警接收消息通道名称
     */
    String ES_ALARM_INPUT = "es_alarm_input";

    /**
     * 缺省发送消息通道
     *
     * @return channel 返回缺省信息发送通道
     */
    @Output(ES_DEFAULT_OUTPUT)
    MessageChannel sendEsDefaultMessage();

    /**
     * 告警发送消息通道
     *
     * @return channel 返回告警信息发送通道
     */
    @Output(ES_ALARM_OUTPUT)
    MessageChannel sendEsAlarmMessage();

    /**
     * 缺省接收消息通道
     *
     * @return channel 返回缺省信息接收通道
     */
    @Input(ES_DEFAULT_INPUT)
    MessageChannel recieveEsDefaultMessage();

    /**
     * 告警接收消息通道
     *
     * @return channel 返回告警信息接收通道
     */
    @Input(ES_ALARM_INPUT)
    MessageChannel recieveEsAlarmMessage();
}
