package com.android.hhn.kotlintoy.myobject

import android.view.View

/**
 * @author hehaonan @ Zhihu Inc.
 * @since 2022-01-27
 */
class TestObject {
    interface OnClickListener {
        fun onClick(v: View);
    }

    interface OnDoubleClickListener {
        fun onDoubleClick(v: View);
    }

    fun setMethod(m: M) {}

    fun setOnClickListener(listener: OnClickListener) {
    }

    fun setOnDoubleClickListener(listener: OnDoubleClickListener) {
    }
}

interface A {
    fun funA()
}

interface B {
    fun funB()
}

abstract class M {
    abstract fun funM()
}

fun main() {
    val myTest = TestObject()
    myTest.setOnClickListener(object : TestObject.OnClickListener {
        override fun onClick(v: View) {
        }
    })

    val testFun = object : M(), A, B {
        override fun funM() {
        }

        override fun funA() {
        }

        override fun funB() {
        }
    }

    val testMethod = object : M(), TestObject.OnClickListener, TestObject.OnDoubleClickListener {
        override fun onClick(v: View) {
        }

        override fun onDoubleClick(v: View) {
        }

        override fun funM() {
        }
    }
    myTest.setOnClickListener(testMethod)
    myTest.setOnDoubleClickListener(testMethod)
    myTest.setMethod(testMethod)
}