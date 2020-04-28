package com.android.hhn.kotlintoy.file

import java.io.File
import java.nio.file.Files

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/28,4:05 PM ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

fun readFile() {
    val file = File(TestJava.getFileName())
    println(file.readText())
}

fun main() {

    readFile()

//    val str: String? = null
//    println(str?.length ?: -1)
//
//    // String! 是种类型
//    val str1 = TestJava.getStr() // 运行不报错
//    // println(str1.length) // 调用报错
//    // val str2: String = TestJava.getStr() //运行报错 TestJava.getStr() must not be null
//    val str3: String? = TestJava.getStr()
//    println(str3?.length)
}

