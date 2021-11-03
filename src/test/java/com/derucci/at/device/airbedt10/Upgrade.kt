package com.derucci.at.device.airbedt10

import com.derucci.at.common.consts.Envs
import com.derucci.at.common.oauth.OauthTest
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader

class Upgrade {
    val env = Envs.UAT
    @Test
    fun test2(){
        val reader = BufferedReader(FileReader("D:\\t10-upgrade.txt"))
        var bool = true
        while(bool){
            val str = reader.readLine()
            when(str){
                null -> {
                    bool = false
                }
                else -> {
                    bool = true
                    upgrade(str)
                    Thread.sleep(5*1000)
                }
            }
        }
    }

    private fun upgrade(machineCode:String){

        val server = env.baseUrl
        val token = OauthTest().login(env, "17875851786", "544926")

        //密钥secretKey
        //specialMcuAppVersionCode是对应不同版本的依据，以及是否切换服务器的依据

        //如果是升级到本服务器的最新固件版本（硬件版本相同情况下，硬件版本查看方法：假设固件版本为20.14.12，则硬件版本为20），
        //最好不要指定specialMcuAppVersionCode参数，以防出现错误指定情况
        val formBody = FormBody.Builder()
                .add("machineCode",machineCode)
                .add("version","20.14.12")
                .add("type","1")
                .add("secretKey",env.secretKey)
                //切换环境时
//                .add("specialMcuAppVersionCode","airBedT10McuBin-2G4P20F-upto-uat")
                .build()

        val request = Request.Builder()
                .url("$server/elf/V2.0/airBedT10/test/upgrade")
                .addHeader("Authorization", token)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(formBody)               .build()

        val response = OkHttpClient().newCall(request).execute()
        println("设备[$machineCode]执行结果[${response.body()!!.string()}]")
    }

//    @Test
//    fun test3(){
//        println(kotlin.String(Hex.decode("7b226d6574686f64223a2268656172745f726174655f3130736563222c226964223a22433330303032303030304643222c226e756d223a223030314138434632222c22706172616d73223a7b22534e223a22433330303032303030304643222c22554e223a22463130333144363535414343222c22544d223a2231363234363931343035222c22444154415f4e4f575f4c223a2230303030303030303030222c22444154415f424f44595f4c223a2236343634363436343634363436343634363436343634363436343634363436343634363436343634222c22444154415f4e4f575f52223a2230303030303030303030222c22444154415f424f44595f52223a2236343634363436343634363436343634363436343634363436343634363436343634363436343634222c22444154415f505f4c53223a2236343237222c22444154415f505f4c42223a2236343437222c22444154415f505f5253223a2235303939222c22444154415f505f5242223a2235313236222c22444154415f595f4c53223a2237363738222c22444154415f595f4c42223a2237363939222c22444154415f595f5253223a2235343733222c22444154415f595f5242223a2235343938222c2250574d5f505f4c55223a2230222c2250574d5f505f4c44223a2230222c2250574d5f505f5255223a2231222c2250574d5f505f5244223a2230222c2250574d5f595f4c55223a2233222c2250574d5f595f4c44223a2230222c2250574d5f595f5255223a2230222c2250574d5f595f5244223a2230227d2c224d4435223a223332444533314331313935323131374543413536453734434630423939343946222c22414553223a223637394338343339303244433031444630374346453145343045303335333645222c226d63755f76657273696f6e223a2232302e31342e38222c22626c655f76657273696f6e223a22322e372e30227d"), Charset.forName("utf-8")))
//    }
}