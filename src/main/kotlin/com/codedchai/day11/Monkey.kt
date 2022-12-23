package com.codedchai.day11

import java.math.BigInteger

data class Monkey(
  val id: Int,
  val items: List<BigInteger>,
  val itemModifierFunction: (BigInteger) -> BigInteger,
  val testDivisbleBy: Int,
  val onTruePassTo: Int,
  val onFalsePassTo: Int
) {

  fun removeItems(): Monkey {
    return copy(items = emptyList())
  }

  fun addItems(newItems: List<BigInteger>): Monkey {
    return copy(items = items + newItems)
  }

  fun modifyItems(): Monkey {
    val modifiedItems = items.map { itemModifierFunction(it) }
    return copy(items = modifiedItems)
  }

  fun partitionIsDivisibleItems(): Pair<ItemsToNewMonkeyId, ItemsToNewMonkeyId> {
    val (passedTestItems, failedTestItems) = items.partition { (it % testDivisbleBy.toBigInteger()) == BigInteger.ZERO }

    return ItemsToNewMonkeyId(onTruePassTo, passedTestItems) to ItemsToNewMonkeyId(onFalsePassTo, failedTestItems)
  }
}
