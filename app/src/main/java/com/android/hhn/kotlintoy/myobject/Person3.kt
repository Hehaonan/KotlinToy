package com.android.hhn.kotlintoy.myobject

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-01-27
 */
class Person3 private constructor(name: String) {
    companion object {
        @Volatile
        private var INSTANCE: Person3? = null

        fun getInstance(name: String): Person3 =
            // 第一次check
            INSTANCE ?: synchronized(this) {
                // 第二次check
                INSTANCE ?: Person3(name).also { INSTANCE = it }
            }
    }
}

fun main() {
    // 使用
    Person3.getInstance("test")
}