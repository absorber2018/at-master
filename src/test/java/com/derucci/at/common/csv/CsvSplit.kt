package com.derucci.at.common.csv

import java.util.LinkedList





/**
 * 普通类<br>
 * Csv文件分割
 *
 * @author Dr
 * @create 2021/8/15
 */
object CsvSplit {

    /**
     * 支持引号嵌套，逗号分割
     *
     * 例子
     * "aaa,",小慕，小慕,请说指令
     * 分割后
     *
     * "aaa,"
     * 小慕，小慕
     * 请说指令
     */
    fun split(line: String): Array<String> {
        val fields = LinkedList<String>()
        val alpah = line.toCharArray()
        var isFieldStart = true
        var pos = 0
        var len = 0
        var yinhao = false
        for (c in alpah) {
            if (isFieldStart) {
                len = 0
                isFieldStart = false
            }
            if (c == '\"') {
                yinhao = !yinhao
            }
            if ((c == ',' ) && !yinhao) {
                fields.add(String(alpah, pos - len, len))
                isFieldStart = true
            }else if((pos == alpah.size -1)){
                fields.add(String(alpah, pos - len, len+1))
                isFieldStart = false
            }
            pos++
            len++
        }
        return fields.toTypedArray()
    }

}