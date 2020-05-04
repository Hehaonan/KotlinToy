package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/29,6:23 PM ;<p/>
 * Description: 同步任务;<p/>
 * Other: ;
 */

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        GlobalScope.launch {
            val token = doSomethingUsefulOne()
            doSomethingUsefulTwo(token)
        }.join()
    }
    println("Completed in $time ms")
}

suspend fun doSomethingUsefulOne(): String {
    delay(2000L) // 假设我们在这里做了些有用的事
    println("doSomethingUsefulOne")
    return "token"
}

suspend fun doSomethingUsefulTwo(token: String) {
    delay(1000L)
    println("doSomethingUsefulTwo need：$token")
}