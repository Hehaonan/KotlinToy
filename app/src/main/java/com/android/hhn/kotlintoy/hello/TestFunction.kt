package com.android.hhn.kotlintoy.hello

/**
 * Author: haonan.he ;<p/>
 * Date: 2020/4/27,6:17 PM ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

fun add(a: Int, secondNum: Int = -1): Int {
    return a + secondNum // 可以写为 单表达式函数
}

// 交换函数
fun <T> ArrayList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // “this”对应该列表
    this[index1] = this[index2]
    this[index2] = tmp
}

// 拓展属性
val <T> ArrayList<T>.lastIndex: Int get() = this.size - 1

fun main() {
//    println(add(15)) // 默认参数
//    println(add(15, secondNum = 12)) // 具名参数
//    val result = add(20) // 赋值给变量
//    println(result)
//
//    val list = arrayListOf(1, 2, 3)
//    list.swap(1, 2) // “swap()”内部的“this”会保存“list”的值
//    println(list.toString())
//    println(list.lastIndex)// 直接调用

//    open class Shape
//    class Rectangle: Shape()
//
//    fun Shape.getName() = "Shape"
//    fun Rectangle.getName() = "Rectangle"
//
//    fun printClassName(s: Shape) {
//        println(s.getName())
//    }
//
//    printClassName(Rectangle())
    println("a" + "b")

    val a = 3
    println(a in 1..2)

    for (i in 1..10 step 2) {
        print("$i")
    }

    5 pk 10
}

infix fun Int.pk(num: Int) {
    val result = this - num
    when {
        result > 0 -> println("$this 大于 $num")
        result < 0 -> println("$this 小于 $num")
        else -> println("$this 等于 $num")
    }
}
