package com.android.hhn.kotlintoy.hello

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Integer.parseInt

/**
 * Author: haonan.he ;<p/>
 * Date: 2020-02-10,15:32 ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

// constructor 可以省略 Any 可以省略
class Person constructor(val name: String, val age: Int) : Any() {

    // 初始化属性
    val firstProperty = "First property: $name".also(::println)

    // 初始化块
    init {
        println("First initializer block prints $name")
    }

    // 第二个初始化属性
    val secondProperty = "Second property: ${name.length}".also(::println)

    // 次构造函数
    constructor(name: String) : this(name, 0) {
        // 如果类有主构造函数，每个次构造函数需要委托给主构造函数
        println("second constructor prints")
    }

    // 解构操作
    operator fun component1() = name
    operator fun component2() = age

    infix fun love(person: Person) {
        println("${this.name} love ${person.name}")
    }

}

fun main() {
//    test1 {
//        println("my world${it}")
//    }
//    println("hello")
//    GlobalScope.launch { }

    // val a: Int = 13
    // println(a.plus(12))

    // useWhen(1)

//    loop@ for (i in 1..3) {
//        println("i:$i")
//        for (j in 1..3) {
//            if (j == 2) break@loop
//            println("-> j:$j")
//        }
//    }
//    println("jump loop")

    //testReturn()

    // 创建 不需要new
    val ironMan = Person("Tony", 18);
    // 解构成单独变量
    val (name, age) = ironMan
    println("$name, $age")
    // map解构
    val map = mapOf("key1" to "value1", "key2" to "value2")
    for ((key, value) in map) {
        println("$key -> $value")
    }

    ironMan love Person("小辣椒")

}

fun testReturn() {
//    listOf(1, 2, 3, 4, 5).forEach {
//        if (it == 3) return // 非局部直接返回到 foo() 的调用者
//        print(it)
//    }

//    listOf(1, 2, 3, 4, 5).forEach lit@{
//        if (it == 3) return@lit //@forEach 标签支持与lambda函数同名
//        print(it)
//    }

    listOf(1, 2, 3, 4, 5).forEach(fun(value: Int) {
        if (value == 3) return  // 局部返回到匿名函数的调用者，即 forEach 循环
        print(value)
    })

    println(" this point is unreachable")
}

fun useWhen(x: Any) = when (x) {
    is String -> print("x is String")
    !in 1..10 -> print("x is outside the range")
    is Int -> print("x is Int")
    else -> false
}

inline fun test1(l: (str: String) -> Unit) {
    l.invoke(" -end")
    return
}