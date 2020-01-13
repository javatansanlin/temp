package com.equipment.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: JavaTansanlin
 * @Description: 发布消息
 * @Date: Created in 14:25 2018/8/10
 * @Modified By:
 */

@Component
public class PubMsg {

    private static Logger logger =LoggerFactory.getLogger(PubMsg.class);

    /** 质量 **/
    private static int qos = 2; //只有一次
    /** 服务器地址 */
    @Value("${spring.mqtt.url}")
    private String broker;
    /** 登陆名 */
    @Value("${spring.mqtt.username}")
    private String userName;
    /** 登陆密码 */
    @Value("${spring.mqtt.password}")
    private String passWord;

    /** 获取链接 **/
    private MqttClient connect(String clientId, String userName,
                                      String password) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setConnectionTimeout(10);
        connOpts.setKeepAliveInterval(20);
//		String[] uris = {"tcp://10.100.124.206:1883","tcp://10.100.124.207:1883"};
//		connOpts.setServerURIs(uris);  //起到负载均衡和高可用的作用
        MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
        mqttClient.setCallback(new PushCallback3("pushCallback3"));
        mqttClient.connect(connOpts);
        return mqttClient;
    }

    /** 发布功能 */
    private static void pub(MqttClient sampleClient, String msg,String topic)
            throws MqttPersistenceException, MqttException {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        message.setRetained(false);
        sampleClient.publish(topic, message);
    }

    /** 发布订阅 */
    public void publish(String str,String clientId,String topic) throws MqttException{
        MqttClient mqttClient = connect(clientId,userName,passWord);//链接获取链接

        if (mqttClient != null) {
            pub(mqttClient, str, topic);//发布消息
            logger.info("发布的订阅-->" + str);
        }

        if (mqttClient != null) {
            mqttClient.disconnect();
        }
    }

}

/** 回调内部类 */
class PushCallback3 implements MqttCallback {
    private String threadId;
    public PushCallback3(String threadId){
        this.threadId = threadId;
    }

    public void connectionLost(Throwable cause) {

    }

    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());
        System.out.println(threadId + "----" + msg);
    }
}
