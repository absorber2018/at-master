package com.derucci.at.common.mqtt

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*


class LastWish {

    companion object {
        private var username: String = ""
        private var password: String = ""
        private var url = arrayOf("tcp://testdev.derucci-smart.com:1883")
        private var defaultTopic = arrayOf("d/p/airBedT10/C300020001A3")
        private var clientIdRandom: String? = null
        private var persistence = MemoryPersistence()

        //clintId存在120s
        //连接测试环境
        //超过120s或者onSuccess函数收到信息时
        //发送遗愿
        //断开连接

        @JvmStatic
        fun main(args: Array<String>) {
            sub()
        }

        //        @Test
        fun sub() {
            if (clientIdRandom == null) {
                clientIdRandom = "test_tools_" + UUID.randomUUID().toString().replace("-", "")
            }
            var client = MqttAsyncClient(url[0], clientIdRandom, persistence)
            // MQTT 连接选项
            val connOpts = MqttConnectOptions()
            connOpts.userName = username
            connOpts.password = password.toCharArray()
            // 保留会话
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
            connOpts.isCleanSession = true
            connOpts.maxInflight = 100
            connOpts.isAutomaticReconnect = true

            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*120秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            connOpts.keepAliveInterval = 20
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //遗愿
            connOpts.setWill("test", "lost the connection".toByteArray(), 1, true)

            // 建立连接
//            client.connect(connOpts)
            client.connect(connOpts, object : IMqttActionListener {
                    //监听器快速返回控制
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                    println("连接成功...")
//                    Thread.sleep(5000)
                    //断开连接
//                    client.disconnect()
                }
//
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    println("连接失败...")
                }
            })
        }
    }
}


