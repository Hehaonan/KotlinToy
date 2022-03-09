package com.android.hhn.kotlintoy.myobject

import android.util.Log

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-01-27
 */
class Person {
    object Singleton {
        const val FLAG = 100

        @JvmStatic
        fun work() {
        }
    }
}

fun main() {
    Person.Singleton.work()
    Log.d("TAG", "main: ${Person.Singleton.FLAG}")
}