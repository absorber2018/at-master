package com.derucci.at.common.csv

import com.alibaba.fastjson.JSON
import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader

/**
 * 普通类<br>
 * 按摩椅语音
 *
 * @author Dr
 * @create 2021/8/15
 */
class MassageChairSpeech {

    class CommonMassageChairSpeechCmdCollectionConfig{

        var type: String? = null

        /**编号*/
        var no:String? = null

        /**语料*/
        var speech:List<SpeechItem>? = null

        /**语料项*/
        class SpeechItem {
            /**语言*/
            var lang:String? = null
            /**回复语音*/
            var replySpeech:String? = null
            /**下发语音*/
            var ctrlSpeech:List<String>? = null
        }
    }

    @Test
    fun test(){
//        CsvSplit.split("""小慕,"aaa,",小慕，小慕,请说指令,"请说,指令","aaa,aa",小慕，小慕,请说指令,请说，指令""").forEach { println(it) }


        val list = ArrayList<CommonMassageChairSpeechCmdCollectionConfig>()

        val reader = BufferedReader(FileReader("C:\\Users\\Administrator\\Desktop\\GCW1-013MCSCC.csv"))
        var bool = true
        var index = 0
        while(bool){
            val str = reader.readLine()
            when(str){
                null -> {
                    bool = false
                }
                else -> {
                    bool = true
                    if(index >0){
                        val split = CsvSplit.split(str)
                        val speechList = ArrayList<CommonMassageChairSpeechCmdCollectionConfig.SpeechItem>()
                        val chinese = CommonMassageChairSpeechCmdCollectionConfig.SpeechItem()
                        chinese.lang = "chinese"
                        chinese.ctrlSpeech = split[1].split("|")
                        chinese.replySpeech = split[2]
                        speechList.add(chinese)
                        if(split.size > 3){
                            val english = CommonMassageChairSpeechCmdCollectionConfig.SpeechItem()
                            english.lang = "english"
                            english.ctrlSpeech = split[3].split("|").map { it.replace("\"","") }
                            english.replySpeech = split[4].replace("\"","")
                            speechList.add(english)
                        }
                        if(split.size > 5){
                            val cantonese = CommonMassageChairSpeechCmdCollectionConfig.SpeechItem()
                            cantonese.lang = "cantonese"
                            cantonese.ctrlSpeech = split[5].split("|").map { it.replace("\"","") }
                            cantonese.replySpeech = split[6].replace("\"","")
                            speechList.add(cantonese)
                        }
                        val config = CommonMassageChairSpeechCmdCollectionConfig()
                        config.type="CommonMassageChairSpeechCmdCollectionConfig"
                        config.no = split[0]
                        config.speech = speechList
                        list.add(config)
                    }
                }
            }
            index++
        }
        println(JSON.toJSONString(list))
    }
}