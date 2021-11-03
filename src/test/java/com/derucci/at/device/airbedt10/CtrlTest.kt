package com.derucci.at.device.airbedt10

import com.alibaba.fastjson.JSONObject
import com.derucci.at.common.consts.Envs
import com.derucci.at.common.oauth.OauthTest
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 普通类<br>
 * 控制
 *
 * @author Dr
 * @create 2021/7/12
 */
class CtrlTest {

    class St(val token:String, val machineCode:String)

    val env = Envs.UAT

    private lateinit var tokenList: List<St>

    @Before
    fun login(){
        // 将所有用户登录，并将其对应设备，存在一个结构中，多个用户一个list
        tokenList = arrayListOf(
                St(token = OauthTest().login(env, "17875851786", "544926"), machineCode = "C30002000004")
//                St(token = OauthTest().login(env, "17875851786", "544926"), machineCode = "C300020001A3")
//                St(token = OauthTest().login(env, "17875851786", "544926"), machineCode = "C300020001A3")
        )
    }

    @Test
    fun ctrl(){
        //此处
        val ok = OkHttpClient()
        while (true) {
            try{
                tokenList.forEach {
                    val configMode = 1
                    //肩部臀部5~100,5的倍数，每次都要改掉，随机数的方法
                    val bothValue = (1..20).random()*5
                    //腰部5~100,5的倍数，每次都要改掉
                    val waistValue = (1..20).random()*5

                    val body = RequestBody.create(
                            MediaType.parse("application/json"),
                            """
                        {
                            "machineCode": "${it.machineCode}",
                            "partConfigs": [{
                                "config": [$bothValue, $waistValue],
                                "configMode": $configMode,
                                "machineCode": "$it.machineCode}",
                                "machineEntityPartCode": "${it.machineCode}####left"
                            }]
                        }
                    """.trimIndent()
                    )
                    val request = Request.Builder()
                            .url("${env.baseUrl}/elf/V2.0/airBedT10Config/actionSetup")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", it.token)
                            .post(body)
                            .build()
                    val response = ok.newCall(request).execute()

                    val jsonObj = JSONObject.parseObject(response.body()!!.string())
                    println("调用结果${jsonObj.toJSONString()}")
                    assertEquals(jsonObj["code"], "OK", "控制失败:${jsonObj.toJSONString()}")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            //到此循环
            Thread.sleep(3000)
        }
    }
}
