package com.derucci.at.common.mqtt

import com.alibaba.fastjson.JSONObject
import okhttp3.*
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * 普通类<br>
 *
 *
 * @author Dr
 * @create 2021/7/15
 */
class MqttSub {

    companion object {
        private var username: String = ""
        private var password: String = ""
        private var url = arrayOf("tcp://test.derucci-smart.com:1883")
        private var defaultTopic = arrayOf("d/p/airBedT10/C300020001A3")
        private var clientIdRandom: String? = null
        private var persistence = MemoryPersistence()

        private val pathName = "D:/mqtt-info/"

        @JvmStatic
        fun main(args: Array<String>) {
            sub()
        }

        //向WebHook发送post请求，并携带发送的信息
        //机器人发送信息方法1
        fun RobotMessage(content:String){
            var RobotAddr = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=47e66477-6f33-4a26-93bf-aae1b50e98ed"
            var httpclient = HttpClients.createDefault()
            //机器人的链接
            var httpPost = HttpPost(RobotAddr)
            httpPost.addHeader("Content-Type","application/json;charset=utf-8")
            //构建一个json格式字符串textMsg，其内容是接收方需要的参数以及我们要发送的内容
            var textMsg = "{\"msgtype\":\"text\",\"text\":{\"content\":\"$content\"}"

            val jsonObject = JSONObject()
            jsonObject["msgtype"] = "text"
            val text = JSONObject()
            text["content"] = content
            val mentioned_mobile_list = arrayListOf("13437397329")
            text["mentioned_mobile_list"] = mentioned_mobile_list
            jsonObject["text"] = text

            var stringEntity = StringEntity(jsonObject.toString(),"utf-8")

//            var stringEntity = StringEntity(textMsg,"utf-8")
            httpPost.entity = stringEntity
            var response = httpclient.execute(httpPost)
            if (response.statusLine.statusCode.equals(HttpStatus.SC_OK)){
                var str = EntityUtils.toString(response.entity,"utf-8")
                println(str)
            }
        }
        //机器人发送信息方法2
        fun RobotMessage1(content: String){
            var RobotAddr = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=47e66477-6f33-4a26-93bf-aae1b50e98ed"
            //创建OkHttpClient对象
            var client = OkHttpClient.Builder().build()
            val mediaType = MediaType.parse("application/json")
            //发送的信息
            val jsonObject = JSONObject()
            jsonObject["msgtype"] = "text"
            val text = JSONObject()
            text["content"] = content
            val mentioned_mobile_list = arrayListOf("13437397329")
            text["mentioned_mobile_list"] = mentioned_mobile_list
            jsonObject["text"] = text
            //创建一个请求体
            val body = RequestBody.create(mediaType, jsonObject.toString())
            //创建请求对象并添加请求参数信息
            var request = Request.Builder()
                    .url(RobotAddr)
                    .method("POST",body)
                    .addHeader("Content-Type","application/json")
                    .build()
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            println(response?.message())
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
                    val fileFolder = current.format(DateTimeFormatter.BASIC_ISO_DATE)

                    //输出信息
                    val content = "当前时间："+formatted.toString() + "，MQTT信息：" + String(p1.payload, Charset.forName("utf-8"))

                    //企业微信机器人提示信息
                    val RobotMsg = "【${p0}设备掉线】\n$formatted\n${String(p1.payload, Charset.forName("utf-8"))}"

                    if(content.contains("mqtt_offline",ignoreCase = true)){
                        //存入文件中
                        val fileName = current.format(DateTimeFormatter.ofPattern("MM-dd-HH"))
                        var file = "$pathName$fileName.txt"
                        File(file).appendText(content)

                        //机器人提示信息
                        RobotMessage(RobotMsg)

                    }
                    println("当前时间："+formatted.toString() + "，MQTT信息：" +String(p1.payload, Charset.forName("utf-8")))
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