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

            startTime = System.currentTimeMillis() //获取开始时间

            // 设置回调
            client.setCallback(object : MqttCallback {
                //收到 broker 新消息
                override fun messageArrived(p0: String?, p1: MqttMessage) {
                    val current = LocalDateTime.now()
                    val formatted = current.format(DateTimeFormatter.ofPattern("YY/MM/dd HH:mm:ss"))

                    //输出信息
                    val content = "当前时间："+formatted.toString() + "，MQTT信息：" + String(p1.payload, Charset.forName("utf-8"))

                    endTime = System.currentTimeMillis() //获取结束时间
                    val internalTime = ((endTime - startTime) /1000).toString() //间隔时间段（s）
                    countDeserve = (internalTime.toInt()/10)
                    if(content.contains("mqtt_offline",ignoreCase = true) || internalTime.toInt() >= 60 * 60){ // 分钟
                        if(content.contains("mqtt_offline",ignoreCase = true)){
                            println(content)
                        }
                        //当count小于0时，理论上发送的数据比实际发送的数据少，此时视为正常发送数据
                        var count = countDeserve.toDouble() - countTruth.toDouble()
                        if(count < 0.0){
                            count = 0.0
                        }
                        System.out.println("理论发送数据： " + countDeserve + "条")
                        System.out.println("实际发送数据： " + countTruth + "条")
                        System.out.println("丢包率： " + count/ countDeserve.toDouble()*100 + "%")
                        System.out.println("程序运行时间： " + internalTime + "s")

                        //取消订阅
                        defaultTopic.forEach {
                            client.unsubscribe(it)
                        }

                        //断开连接
                        client.disconnect()
                    }
                    if (content.contains("heart_rate_10sec")) {
                        countTruth++
                    }
                    if (content.contains("heart_rate_10sec",ignoreCase = true)){
                        println(content)
                    }
                }

                //与 broker 连接丢失
                override fun connectionLost(p0: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                //消息到 broker 传递完成
                override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

            // 建立连接
            client.connect(connOpts)

            // 订阅
            defaultTopic.forEach {
                client.subscribe(it)
            }

            //取消订阅
//        defaultTopic.forEach {
//            client.unsubscribe(it)
//        }

            //断开连接
//        client.disconnect()
        }
    }
}