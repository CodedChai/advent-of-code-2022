package com.codedchai.day11

import java.io.File
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
    val items = monkeyData[1].removePrefix("  Starting items: ").split(", ").map { it.toInt() }
    val operation = buildItemModifierFunction(monkeyData[2])
    val test = monkeyData[3].getLastInt()
    val onTruePassTo = monkeyData[4].getLastInt()
    val onFalsePassTo = monkeyData[5].getLastInt()

    return Monkey(id, items, operation, test, onTruePassTo, onFalsePassTo)
  }

  fun String.toIntOp(): Int.(Int) -> Int = when (this.trim()) {
    "+" -> Int::plus
    "-" -> Int::minus
    "*" -> Int::times
    "/" -> Int::div
    else -> error("Unknown operator $this")
  }

  fun buildItemModifierFunction(line: String): (Int) -> Int {
    val split = line.split(" ")
    val operator = split[split.size - 2].toIntOp()

    return if (split.last().equals("old", true)) {
      fun(old: Int): Int {
        return floor(operator(old, old) / 3.0).toInt()
      }
    } else {
      val num = split.last().toInt()
      fun(old: Int): Int {
        return floor(operator(old, num) / 3.0).toInt()
      }
    }
  }

  fun String.getLastInt(): Int {
    return split(" ").last().toInt()
  }
}

data class Monkey(
  val id: Int,
  val items: List<Int>,
  val itemModifierFunction: (Int) -> Int,
  val testDivisbleBy: Int,
  val onTruePassTo: Int,
  val onFalsePassTo: Int
) {

  fun removeItems(): Monkey {
    return copy(items = emptyList())
  }

  fun addItems(newItems: List<Int>): Monkey {
    return copy(items = items + newItems)
  }

  fun modifyItems(): Monkey {
    val modifiedItems = items.map { itemModifierFunction(it) }
    return copy(items = modifiedItems)
  }

  fun partitionIsDivisibleItems(): Pair<ItemsToNewMonkeyId, ItemsToNewMonkeyId> {
    val (passedTestItems, failedTestItems) = items.partition { it % testDivisbleBy == 0 }

    return ItemsToNewMonkeyId(onTruePassTo, passedTestItems) to ItemsToNewMonkeyId(onFalsePassTo, failedTestItems)
  }
}

data class ItemsToNewMonkeyId(
  val newId: Int,
  val items: List<Int>
)

fun main() {
  MonkeyBusinessPart1().process()
}