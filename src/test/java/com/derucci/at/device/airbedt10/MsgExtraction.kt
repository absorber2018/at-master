package com.derucci.at.device.airbedt10

import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintStream
import java.util.regex.Matcher
import java.util.regex.Pattern

class MsgExtraction {
    @Test
    fun test() {
        val reader = BufferedReader(FileReader("D:\\实习\\T10错误列表.csv"))
        var bool = true
        reader.readLine()
        val printStream = PrintStream("D:\\MsgExtraction.csv")
        printStream.println("_id,machineInfoNo,machineCode,createAt,label,machineTimestamp,recordData.drpt,recordData.data")
        while (bool) {
            val str = reader.readLine()
            when (str) {
                null -> {
                    bool = false
                }
                else -> {
                    bool = true
                    val msg1 = str.substring(0, str.indexOf('"'))
                    val msg2 = str.substring(str.indexOf("["),str.indexOf("]")+1)
//                    println(key)
                    val msg = """$msg1"$msg2"""".trimIndent()
                    printStream.println(msg)
                    println(msg)
                }
            }
        }
        printStream.close()

    }
}