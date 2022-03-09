package com.android.hhn.kotlintoy.myobject

import android.util.Log

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-01-27
 */
class Person2 {
    companion object Singleton {
        const val FLAG = 100

        @JvmStatic
        fun work() {
        }
    }
}

fun main() {
    Person2.work()
    Log.d("TAG", "main: ${Person2.FLAG}")
}