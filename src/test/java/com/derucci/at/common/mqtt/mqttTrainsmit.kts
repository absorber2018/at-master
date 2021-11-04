package com.derucci.at.common.mqtt

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class TestPacketLoss {

    companion object {
        private var username: String = ""
        private var password: String = ""
        private var url = arrayOf("tcp://test.derucci-smart.com:1883")
        private var defaultTopic = arrayOf("d/p/airBedT10/C300020000E9","d/w/airBedT10/C300020000E9")
        private var usernamePubTo: String = ""
        private var passwordPubTo: String = ""
        private var urlPubTo = arrayOf("tcp://devtest.derucci-smart.com:1883")
        //private var pubDefaultTopic = arrayOf("d/p/airBedT10/C300020000E9","d/w/airBedT10/C300020000E9")
        private var clientIdRandom: String? = null
        private var persistence = MemoryPersistence()
        var startTime = System.currentTimeMillis()
        var endTime = System.currentTimeMillis()

        //理论获得的消息数
        var countDeserve = 0
        //实际获得的消息数
        var countTruth = 0

        @JvmStatic
        fun main(args: Array<String>) {
            sub()
        }

        //    @Test
        fun sub() {
            if (clientIdRandom == null) {
                clientIdRandom = "test_tools_" + UUID.randomUUID().toString().replace("-", "")
            }
            val client = MqttClient(url[0], clientIdRandom, persistence)
            // MQTT 连接选项
            val connOpts = MqttConnectOptions()
            connOpts.userName = username
            connOpts.password = password.toCharArray()
            // 保留会话
            connOpts.isCleanSession = true
            connOpts.maxInflight = 100
            connOpts.isAutomaticReconnect = true
            // 建立连接
            client.connect(connOpts)
            // 订阅
            defaultTopic.forEach {
                client.subscribe(it)
            }



            //pub服务器连接
            if (clientIdRandom == null) {
                clientIdRandom = "test_tools_" + UUID.randomUUID().toString().replace("-", "")
            }
            val clientPubTo = MqttClient(urlPubTo[0], clientIdRandom, persistence)
            // MQTT 连接选项
            val connOptsPubTo = MqttConnectOptions()
            connOptsPubTo.userName = usernamePub
            connOptsPubTo.password = passwordPub.toCharArray()
            // 保留会话
            connOptsPubTo.isCleanSession = true
            connOptsPubTo.maxInflight = 100
            connOptsPubTo.isAutomaticReconnect = true

            //startTime = System.currentTimeMillis() 获取开始时间

            // 设置回调



            //取消订阅
//        defaultTopic.forEach {
//            client.unsubscribe(it)
//        }

            //断开连接
//        client.disconnect()
        }
    }
}