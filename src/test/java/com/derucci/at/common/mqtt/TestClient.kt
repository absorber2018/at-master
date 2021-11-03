package com.derucci.at.common.mqtt

import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

class TestClient {
    companion object {
        private var username: String = ""
        private var password: String = ""
        private var url = arrayOf("tcp://testdev.derucci-smart.com:1883")
        private var clientIdRandom = "test_tools_" + UUID.randomUUID().toString().replace("-", "")
        private var persistence = MemoryPersistence()
        private var client = MqttAsyncClient(url[0], clientIdRandom, persistence)

        @JvmStatic
        fun main(args: Array<String>) {
            sub()
        }

        //    @Test
        fun sub() {
            // MQTT 连接选项
            val connOpts = MqttConnectOptions()
            connOpts.userName = username
            connOpts.password = password.toCharArray()
            // 保留会话
            connOpts.isCleanSession = true
            connOpts.maxInflight = 100

            connOpts.isAutomaticReconnect = true


            // 建立连接
            client.connect(connOpts, object : IMqttActionListener {
                //监听器快速返回控制
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    println("连接成功...")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    println("连接失败")
                }
            })

        }
    }
}