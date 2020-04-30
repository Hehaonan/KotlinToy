package com.android.hhn.kotlintoy.coroutines

import kotlinx.coroutines.*

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/29,6:32 PM ;<p/>
 * Description: ;<p/>
 * Other: ;
 */


fun main() = runBlocking {
    test1()
}

suspend fun test2() {
    GlobalScope.launch {
        val result1 = GlobalScope.async {
            getResult1()
        }
        val result2 = GlobalScope.async {
            getResult2()
        }
        val result = result1.await() + result2.await()
        println("result = $result")
    }
}


private suspend fun getResult1(): Int {
    delay(3000)
    return 1
}

private suspend fun getResult2(): Int {
    delay(4000)
    return 2
}


suspend fun test1() {
    GlobalScope.launch {
        val token = getToken()
        val userInfo = getUserInfo(token)
        setUserInfo(userInfo)
    }.join()
}

private fun setUserInfo(userInfo: String) {
    println(userInfo)
}

private suspend fun getToken(): String {
    delay(2000)
    return "token"
}

private suspend fun getUserInfo(token: String): String {
    delay(3000)
    return "$token - userInfo"
}
