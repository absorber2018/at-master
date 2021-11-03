package com.derucci.at.device.airbedt10

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.derucci.at.common.mqtt.MqttSub
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FlipCount {
    companion object {
        private var username: String = "testUser"
        private var password: String = "werdsvxcvfhcgh"
        private var url = arrayOf("tcp://web.derucci-smart.com:1883")
        private var defaultTopic = arrayOf("d/c/airBedT10/C300020001A3/test/tRtAgOut")
        private var clientIdRandom: String? = null
        private var persistence = MemoryPersistence()

        var count1 = 0
        var count2 = 0

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


            // 设置回调
            client.setCallback(object : MqttCallback {
                //收到 broker 新消息
                override fun messageArrived(p0: String?, p1: MqttMessage) {
                    val current = LocalDateTime.now()
                    val formatted = current.format(DateTimeFormatter.ofPattern("YY/MM/dd HH:mm:ss"))

                    //解析数据
                    var temp = JSON.parseObject(String(p1.payload, Charset.forName("utf-8")))
                    temp = JSON.parseObject(temp.getString("obj"))
                    var jsonArray = temp.getJSONArray("rtData")
                    var string: String = jsonArray.getString(0)
                    var fromObject: JSONObject = JSONObject.parseObject(string)
                    var key1 = fromObject.getInteger("adjuststatus")
                    string = jsonArray.getString(1)
                    fromObject = JSONObject.parseObject(string)
                    var key2 = fromObject.getInteger("adjuststatus")

                    var file = "D:/mqtt-info.txt"
                    var content = ""

                    if(formatted.substring(9,11).toInt() > 19 || formatted.substring(9,11).toInt() < 10) {
                        if (key1 == 2) {
                            count1++
                            content = "当前时间：$formatted，左侧用户翻身了，至今总翻身次数：$count1，MQTT信息："+String(p1.payload, Charset.forName("utf-8"))
                            println(content)
                            File(file).appendText(content + "\n")
                        }
                        if (key2 == 2) {
                            count2++
                            content = "当前时间：$formatted，右侧用户翻身了，至今总翻身次数：$count2，MQTT信息："+String(p1.payload, Charset.forName("utf-8"))
                            println(content)
                            File(file).appendText(content + "\n")
                        }
                    }else{
                        content = "截止至$formatted，左侧用户翻身次数：$count1，右侧用户翻身次数：$count2"
                        println(content)
                        File(file).appendText(content + "\n")

                        //取消订阅
                        defaultTopic.forEach {
                            client.unsubscribe(it)
                        }

                        //断开连接
                        client.disconnect()
                    }
                }

                //与 broker 连接丢失
                override fun connectionLost(p0: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    println("连接丢失，正在重新连接...")
                    // 建立连接
                    client.connect(connOpts)

                    // 订阅
                    defaultTopic.forEach {
                        client.subscribe(it)
                    }
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