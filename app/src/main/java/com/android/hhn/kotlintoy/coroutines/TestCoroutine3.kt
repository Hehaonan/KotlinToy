package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/29,6:32 PM ;<p/>
 * Description: 测试异步任务;<p/>
 * Other: ;
 */
fun main() = runBlocking {
    val time = measureTimeMillis {
        val one = GlobalScope.async { getData1() }
        val two = GlobalScope.async { getData2() }
        println("The answer is ${one.await() + "-" + two.await()}")
    }
    println("Completed in $time ms")
}

private suspend fun getData1(): String {
    delay(1000)
    return "data1"
}

private suspend fun getData2(): String {
    delay(2000)
    return "data2"
}
