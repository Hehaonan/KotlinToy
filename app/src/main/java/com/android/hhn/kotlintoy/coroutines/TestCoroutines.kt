package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/28,6:05 PM ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

fun main() = runBlocking {

//    GlobalScope.launch { // 在后台启动一个新的协程并继续
//        delay(1000L)
//        println("子线程：World!")
//    }
//    println("主线程：Hello,") // 主线程中的代码会立即执行
//    runBlocking {     // 但是这个表达式阻塞了主线程
//        delay(2000L)  // 我们延迟 2 秒来保证 JVM 的存活
//    }
    // Thread.sleep(2000L) 等价于sleep方法

    println("主线程id：${Thread.currentThread().id}")
    val job = GlobalScope.launch {
        delay(1000)
        println("协程执行结束 -- 线程id：${Thread.currentThread().id}")
    }
    println("主线程执行结束")
    job.join();

}


//fun main() = runBlocking {
//    val job = GlobalScope.launch { // 启动一个新协程并保持对这个作业的引用
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    job.join() // 等待直到子协程执行结束
//}

//fun main() = runBlocking {
//    GlobalScope.launch {
//        repeat(10) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//    delay(2000L) // 在延迟后退出
//}