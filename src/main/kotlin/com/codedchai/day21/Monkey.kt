package com.codedchai.day21

sealed class Monkey {
  abstract val name: String

  data class JobMonkey(
    override val name: String,
    val leftMonkeyName: String,
    val rightMonkeyName: String,
    val operatorFun: OperatorFunction,
    var result: Long? = null
  ) : Monkey()

  data class YellingMonkey(
    override val name: String,
    val number: Long
  ) : Monkey()

  data class Human(
    override val name: String,
    var number: Long? = null
  ) : Monkey()
}