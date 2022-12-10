package com.codedchai.day01

import java.io.File

fun main() {
  var elfNumber = 1
  val elfToCalories = mutableMapOf(elfNumber to 0)
  File("resources/day1/input.txt").forEachLine {
    val calories = it.toIntOrNull()

    if (calories == null) {
      elfNumber++
      elfToCalories[elfNumber] = 0
    } else {
      elfToCalories[elfNumber] = (elfToCalories[elfNumber] ?: 0) + calories
    }
  }

  println("Part 2 answer: " + elfToCalories.values.sortedDescending().take(3).sum())
  val answer = elfToCalories.values.max()
  println(answer)
}