package com.codedchai.day21

import java.io.File

class MonkeyMathPart2 {
  fun theyDidTheHumanMath() {
    val monkeyMap = File("resources/day21/input.txt").readLines()
      .map { buildMonkey(it) }.associateBy { it.name }

    val rootMonkey = monkeyMap["root"]!! as Monkey.JobMonkey

    val leftNum = calculateValueOfMonkey(rootMonkey.leftMonkeyName, monkeyMap)
    val rightNum = calculateValueOfMonkey(rootMonkey.rightMonkeyName, monkeyMap)

    val desiredResult = leftNum ?: rightNum!!

    if (leftNum == null) {
      (monkeyMap[rootMonkey.leftMonkeyName]!! as Monkey.JobMonkey).result = rightNum!!
    }
    if (rightNum == null) {
      (monkeyMap[rootMonkey.rightMonkeyName]!! as Monkey.JobMonkey).result = leftNum!!
    }

    val nullMonkeyName = if (leftNum == null) {
      rootMonkey.leftMonkeyName
    } else {
      rootMonkey.rightMonkeyName
    }

    resultToNumber(nullMonkeyName, monkeyMap, desiredResult)
  }

  fun calculateValueOfMonkey(monkeyName: String, monkeyMap: Map<String, Monkey>): Long? {
    val monkey = monkeyMap[monkeyName]!!
    return when (monkey) {
      is Monkey.Human -> null
      is Monkey.YellingMonkey -> monkey.number
      is Monkey.JobMonkey -> {
        if (monkey.result != null) {
          return monkey.result
        }
        val leftMonkeyValue = calculateValueOfMonkey(monkey.leftMonkeyName, monkeyMap)
        val rightMonkeyValue = calculateValueOfMonkey(monkey.rightMonkeyName, monkeyMap)
        leftMonkeyValue ?: return null
        rightMonkeyValue ?: return null
        val result = monkey.operatorFun.toOp().invoke(leftMonkeyValue, rightMonkeyValue)
        monkey.result = result
        result
      }
    }
  }

  fun resultToNumber(name: String, monkeyMap: Map<String, Monkey>, currentValue: Long): Long {
    val monkey = monkeyMap[name]!!
    println(monkey)
    return when (monkey) {
      is Monkey.Human -> {
        println("Answer: $currentValue")
        monkey.number = currentValue
        currentValue
      }

      is Monkey.YellingMonkey -> monkey.number
      is Monkey.JobMonkey -> {
        val leftMonkeyValue = calculateValueOfMonkey(monkey.leftMonkeyName, monkeyMap)
        val rightMonkeyValue = calculateValueOfMonkey(monkey.rightMonkeyName, monkeyMap)

        val newValue = if (leftMonkeyValue != null) {
          when (monkey.operatorFun) {
            OperatorFunction.MINUS -> reverseLeftMinus(leftMonkeyValue, currentValue)
            OperatorFunction.DIV -> reverseLeftDiv(leftMonkeyValue, currentValue)
            OperatorFunction.PLUS -> reversePlus(leftMonkeyValue, currentValue)
            OperatorFunction.TIMES -> reverseTimes(leftMonkeyValue, currentValue)
          }
        } else {
          when (monkey.operatorFun) {
            OperatorFunction.MINUS -> reverseRightMinus(rightMonkeyValue!!, currentValue)
            OperatorFunction.DIV -> reverseRightDiv(rightMonkeyValue!!, currentValue)
            OperatorFunction.PLUS -> reversePlus(rightMonkeyValue!!, currentValue)
            OperatorFunction.TIMES -> reverseTimes(rightMonkeyValue!!, currentValue)
          }
        }
        monkey.result = newValue
        val nullMonkeyName = if (leftMonkeyValue == null) {
          monkey.leftMonkeyName
        } else {
          monkey.rightMonkeyName
        }

        resultToNumber(nullMonkeyName, monkeyMap, newValue)
      }
    }
  }

  fun OperatorFunction.toReverseOp(): Long.(Long) -> Long = when (this) {
    OperatorFunction.PLUS -> Long::plus
    OperatorFunction.MINUS -> Long::div
    OperatorFunction.TIMES -> Long::times
    OperatorFunction.DIV -> Long::div
  }

  // 10 = 5 * x -> 10/5 = 2 (result divided by non null num)
  fun reverseTimes(number: Long, topResult: Long): Long {
    return topResult / number
  }

  // 10 / x = 2 -> 10 / 2 = 5 (so if its null on right side you divide num by result)
  fun reverseLeftDiv(number: Long, topResult: Long): Long {
    return number / topResult
  }

  // x / 2 = 10 -> 10 * 2 = 20 (so if its on left side you multiply num * result)
  fun reverseRightDiv(number: Long, topResult: Long): Long {
    return number * topResult
  }

  // 10 - x = 8 -> 10 - 8 = 2 (so if its on right side num - result)
  fun reverseLeftMinus(number: Long, topResult: Long): Long {
    return number - topResult
  }

  // x - 5 = 10 -> 10 + 5 = 15 (so if its on left side add num & result)
  fun reverseRightMinus(number: Long, topResult: Long): Long {
    return number + topResult
  }

  // 10 + x = 15 -> 5 = 15 - 10 (result - non null num)
  fun reversePlus(number: Long, topResult: Long): Long {
    return topResult - number
  }

  fun buildMonkey(line: String): Monkey {
    val splitLine = line.split(" ")
    val name = splitLine.first().substring(0..3)
    return if (splitLine.size == 2) {
      if (name == "humn") {
        buildHuman(name)
      } else {
        buildYellingMonkey(name, splitLine[1])
      }
    } else {
      buildJobMonkey(name, splitLine)
    }
  }

  fun buildHuman(name: String): Monkey.Human {
    return Monkey.Human(name)
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
  MonkeyMathPart2().theyDidTheHumanMath()
}