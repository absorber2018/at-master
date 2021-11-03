package com.derucci.at.common.consts

/**
 * 枚举<br>
 * 环境
 *
 * @author Dr
 * @create 2021/7/12
 */
enum class Envs(val baseUrl: String, val username: String, val password: String, val clientId: String, val clientSecret:String,val secretKey:String){

    /**UAT环境*/
    UAT(baseUrl = "https://test.derucci-smart.com",username = "13580774707",password = "123456",clientId = "derucci",clientSecret = "derucci",secretKey="1234567"),
    /**生产环境*/
    PROD(baseUrl = "https://www.derucci-smart.com",username = "13580774707",password = "123456",clientId = "derucci",clientSecret = "derucci",secretKey="587412369")

}