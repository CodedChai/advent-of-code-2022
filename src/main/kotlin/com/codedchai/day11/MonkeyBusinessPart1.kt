package com.codedchai.day11

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.floor

class MonkeyBusinessPart1 {
  fun process() {
    val monkeys = File("resources/day11/input.txt").readLines().chunked(7).mapNotNull { monkeyData ->
      buildMonkey(monkeyData)
    }

    val monkeyMap = monkeys.associateBy { it.id }.toMutableMap()

    monkeys.forEach { println(it) }

    val monkeyInspectionCount = mutableMapOf<Int, Int>()

    for (round in 1..20) {
      for (turnCounter in monkeys.indices) {
        val currentMonkey = monkeyMap[turnCounter]!!
        monkeyInspectionCount[currentMonkey.id] = (monkeyInspectionCount[currentMonkey.id] ?: 0) + currentMonkey.items.count()
        val (newItemsToTrue, newItemsToFalse) = currentMonkey.modifyItems().partitionIsDivisibleItems()
        monkeyMap[turnCounter] = currentMonkey.removeItems()
        monkeyMap[newItemsToTrue.newId] = monkeyMap[newItemsToTrue.newId]!!.addItems(newItemsToTrue.items)
        monkeyMap[newItemsToFalse.newId] = monkeyMap[newItemsToFalse.newId]!!.addItems(newItemsToFalse.items)
      }
      println("Round: $round")
      monkeyMap.forEach { (_, monkey) -> println(monkey) }
      println("----------------------------------------------")
    }

    println("Inspection counts: $monkeyInspectionCount")
    val amountOfMonkeyBusiness = monkeyInspectionCount.map { (_, inspectionCount) -> inspectionCount }.sortedDescending().take(2).reduceRight { a, b -> a * b }
    println("Total monkey business: $amountOfMonkeyBusiness")
  }

  fun buildMonkey(monkeyData: List<String>): Monkey? {
    if (monkeyData.size < 6) return null
    val id = monkeyData[0].removePrefix("Monkey ").removeSuffix(":").toInt()
    val items = monkeyData[1].removePrefix("  Starting items: ").split(", ").map { it.toBigInteger() }
    val operation = buildItemModifierFunction(monkeyData[2])
    val test = monkeyData[3].getLastInt()
    val onTruePassTo = monkeyData[4].getLastInt()
    val onFalsePassTo = monkeyData[5].getLastInt()

    return Monkey(id, items, operation, test, onTruePassTo, onFalsePassTo)
  }

  fun String.toOp(): BigInteger.(BigInteger) -> BigInteger = when (this.trim()) {
    "+" -> BigInteger::plus
    "-" -> BigInteger::minus
    "*" -> BigInteger::times
    "/" -> BigInteger::div
    else -> error("Unknown operator $this")
  }

  fun buildItemModifierFunction(line: String): (BigInteger) -> BigInteger {
    val split = line.split(" ")
    val operator = split[split.size - 2].toOp()

    return if (split.last().equals("old", true)) {
      fun(old: BigInteger): BigInteger {
        return floor(operator(old, old).toBigDecimal().div(BigDecimal(3.0)).toDouble()).toLong().toBigInteger()
      }
    } else {
      val num = split.last().toBigInteger()
      fun(old: BigInteger): BigInteger {
        return floor(operator(old, num).toBigDecimal().div(BigDecimal(3.0)).toDouble()).toLong().toBigInteger()
      }
    }
  }

  fun String.getLastInt(): Int {
    return split(" ").last().toInt()
  }
}

fun main() {
  MonkeyBusinessPart1().process()
}