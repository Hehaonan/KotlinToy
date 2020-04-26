package com.android.hhn.kotlintoy.rang

/**
 * Author: haonan.he ;<p/>
 * Date: 2020-03-11,15:43 ;<p/>
 * Description: ;<p/>
 * Other: ;
 */

class Version(val major: Int, val minor: Int) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        if (this.major != other.major) {
            return this.major - other.major
        }
        return this.minor - other.minor
    }
}

fun main() {

//    val versionRange = Version(1, 11)..Version(1, 30)
//    println(Version(0, 9) in versionRange)
//    println(Version(1, 20) in versionRange)

    // .. 运算符重载，operator修饰，形式：operator fun rangeTo(other: Int)
    // 运算符的重载需要根据已有的函数能力重载，不能凭空创造，入String的"+"符号，重载的是String.plus函数。

    // step 中缀表达式，infix 修饰，形式：infix fun IntProgression.step(step: Int)
    // 中缀表达式可以用来拓展函数，可以随意定义，形式：类名大写 + .符号 来声明，例如：Int.ko(num: Int)

    // 两者可以理解为都是特殊的函数

//    val numbers = listOf("one", "two", "three", "four")
//    println(numbers.associateBy { it.first().toUpperCase() })
//
//    val numberList = listOf("one", "two", "three", "four", "five")
//
//    val numbers2 = listOf("one", "two", "three", "four", "five")
//
//    println(numbers2.groupBy { it.first().toUpperCase() })
//    println(numbers2.groupBy(keySelector = { it.first() }, valueTransform = { it.toUpperCase() }))

    val numbers = listOf("one", "two", "three", "four", "five")
    println(numbers.windowed(3))


    val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key11" to 11)
    numbersMap.forEach { (key, value) -> println("$key < > $value") }

    val numbers3 = listOf("one", "two", "three", "four", "five")
    println(numbers3.zipWithNext())
    println(numbers3.zipWithNext() { s1, s2 -> s1.length > s2.length })

}