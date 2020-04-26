package com.android.hhn.kotlintoy.hello

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Author: haonan.he ;<p/>
 * Date: 2020-02-10,15:32 ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

fun main() {
    test1 {
        println("my world${it}")
    }
    println("hello")
    GlobalScope.launch { }
}

inline fun test1(l: (str: String) -> Unit) {
    l.invoke(" -end")
    return
}