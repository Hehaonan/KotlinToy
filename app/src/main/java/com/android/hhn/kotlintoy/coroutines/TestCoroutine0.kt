package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/30,3:21 PM ;<p/>
 * Description: 阻塞的例子;<p/>
 * Other:
 * 推荐用Android项目去测试，能好理解非阻塞的效果;
 * main方法执行完主进程就挂了，不会管协程的状态，协程的生命周期是受整个应用程序的生命周期限制。;
 */
fun main() {
    val job = GlobalScope.launch { // 启动一个新的协程并继续
        delay(1000L) // 如果时间大于阻塞时间 协程就随着主进程去世了
        println("子线程 ${Thread.currentThread().id} ：World!")
    }
    println("是否存活:${job.isActive}")
    println("主线程 ${Thread.currentThread().id} ：Hello,")
    runBlocking {// 阻塞主线程
        delay(2000L) // 延迟 2 秒来保证 JVM 的存活
        // 等价于 Thread.sleep(2000L)
    }
    println("是否存活:${job.isActive}")
}

//
//fun main() {
//    println("主线程 ${Thread.currentThread().id} ：Hello,")
//    runBlocking {// 阻塞主线程
//        delay(2000L) // 延迟 2 秒来保证 JVM 的存活
//        println("子线程 ${Thread.currentThread().id} ：World")
//        // 等价于 Thread.sleep(2000L)
//    }
//}