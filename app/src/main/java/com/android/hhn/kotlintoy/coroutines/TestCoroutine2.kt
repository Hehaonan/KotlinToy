package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/29,6:23 PM ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

object CommonUsage {
    suspend fun suspendFun1(param: Int): Int {
        printFormatMsg("enter suspendFun1()")
        var result = GlobalScope.async {
            suspendFun2(param)
        }
        printFormatMsg("done suspendFun1()")
        return result.await() + 33
    }

    suspend fun suspendFun2(param: Int): Int {
        printFormatMsg("enter suspendFun2()")
        delay(1000)
        printFormatMsg("done suspendFun2()")
        return 15 + param
    }
}

private fun printFormatMsg(str: String) {
    println(str)
}

private fun testCommonUsage() {
    printFormatMsg("enter test")
    runBlocking {
        printFormatMsg("result in runBlocking is ${CommonUsage.suspendFun1(1)}")
    }
    printFormatMsg("done test")
}

fun main() {
    testCommonUsage();
}