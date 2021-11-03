package com.derucci.at.common.oauth

import com.alibaba.fastjson.JSONObject
import com.derucci.at.common.consts.Envs
import kotlin.test.assertTrue
import okhttp3.*
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Route

/**
 * 普通类<br>
 *
 *
 * @author Dr
 * @create 2021/7/12
 */
class OauthTest {

    fun login(env: Envs, username: String, password: String):String{
        val formBody = FormBody.Builder()
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .add("isPwdEncode", "false")
                .add("loginType", "0")
                .build()

        val request = Request.Builder()
                .url("${env.baseUrl}/auth/oauth/token")
                .addHeader("Content-Type","form-data")
                .post(formBody)
                .build()

        val client =buildBasicAuthClient(env.clientId, env.clientSecret)

        val response = client.newCall(request).execute()

        val jsonObj = JSONObject.parseObject(response.body()!!.string())

        assertTrue(jsonObj["access_token"] != null, "登录失败")

        return "Bearer ${jsonObj.getString("access_token")}"
    }

    fun buildBasicAuthClient(name: String, password: String): OkHttpClient {
        return OkHttpClient.Builder().authenticator(object : Authenticator {
            @Throws(IOException::class)
            override fun authenticate(route: Route, response: Response): Request {
                val credential = Credentials.basic(name, password)
                return response.request().newBuilder().header("Authorization", credential).build()
            }
        }).build()
    }
}