package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.*

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/28,6:05 PM ;<p/>
 * Description: 非阻塞的例子;<p/>
 * Other: 使用runBlocking就会造成主线程阻塞，main方法中测试(执行完主进程就挂了，不会等协程的状态)，其实还是取决于job.join的调用时机，
 * 推荐用Android项目去测试，能好理解非阻塞的效果;
 * main方法执行完主进程就挂了，不会管协程的状态，协程的生命周期是受整个应用程序的生命周期限制。
 */
fun main() = runBlocking {
    val job = GlobalScope.launch {
        delay(2000)
        println("协程执行结束 -- 线程id：${Thread.currentThread().id}")
    }
    println("主线程执行结束 -- 线程id：${Thread.currentThread().id}")
    println("是否存活:${job.isActive},是否完成：${job.isCompleted}")
    job.join()// 也是个阻塞方法
    println("是否存活:${job.isActive},是否完成：${job.isCompleted}")
}

//fun main() = runBlocking {
//    GlobalScope.launch {
//        repeat(10) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//    delay(2000L) // 在延迟后退出
//}