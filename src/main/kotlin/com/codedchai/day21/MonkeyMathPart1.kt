package com.codedchai.day21

import java.io.File

class MonkeyMathPart1 {
  fun theyDidTheMonkeyMath() {
    val monkeyMap = File("resources/day21/input.txt").readLines()
      .map { buildMonkey(it) }.associateBy { it.name }

    val rootMonkey = monkeyMap["root"]!!

    val answer = calculateValueOfMonkey(rootMonkey, monkeyMap)
    println(answer)
  }

  fun calculateValueOfMonkey(monkey: Monkey, monkeyMap: Map<String, Monkey>): Long {
    return when (monkey) {
      is Monkey.Human -> throw Error("Should only be in part 2")
      is Monkey.YellingMonkey -> monkey.number
      is Monkey.JobMonkey -> {
        val leftMonkeyValue = calculateValueOfMonkey(monkeyMap[monkey.leftMonkeyName]!!, monkeyMap)
        val rightMonkeyValue = calculateValueOfMonkey(monkeyMap[monkey.rightMonkeyName]!!, monkeyMap)

        monkey.operatorFun.toOp().invoke(leftMonkeyValue, rightMonkeyValue)
      }
    }
  }

  fun buildMonkey(line: String): Monkey {
    val splitLine = line.split(" ")
    val name = splitLine.first().substring(0..3)
    return if (splitLine.size == 2) {
      buildYellingMonkey(name, splitLine[1])
    } else {
      buildJobMonkey(name, splitLine)
    }
  }

  fun buildJobMonkey(name: String, splitLine: List<String>): Monkey.JobMonkey {
    return Monkey.JobMonkey(
      name = name,
      leftMonkeyName = splitLine[1],
      rightMonkeyName = splitLine[3],
      operatorFun = splitLine[2].toOperatorFunction()
    )
  }

  fun buildYellingMonkey(name: String, number: String): Monkey.YellingMonkey {
    return Monkey.YellingMonkey(name, number.toLong())
  }

  fun String.toOperatorFunction(): OperatorFunction {
    return when (this) {
      "+" -> OperatorFunction.PLUS
      "-" -> OperatorFunction.MINUS
      "*" -> OperatorFunction.TIMES
      "/" -> OperatorFunction.DIV
      else -> throw Error("Cannot parse $this to an OperatorFunction")
    }
  }

  fun OperatorFunction.toOp(): Long.(Long) -> Long = when (this) {
    OperatorFunction.PLUS -> Long::plus
    OperatorFunction.MINUS -> Long::minus
    OperatorFunction.TIMES -> Long::times
    OperatorFunction.DIV -> Long::div
  }
}

fun main() {
  MonkeyMathPart1().theyDidTheMonkeyMath()
}