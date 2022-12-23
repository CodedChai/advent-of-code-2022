package com.codedchai.day03

import java.io.File

class RucksackPriorityPart2 {
  fun calculatePriority() {
    val lines = File("resources/day03/input.txt").readLines()

    val sum = lines.chunked(3).sumOf { line ->
      val rucksack1 = line[0].toHashSet()
      val rucksack2 = line[1].toHashSet()
      val duplicateChar = line[2].first { it in rucksack1 && it in rucksack2 }
      getCharPriority(duplicateChar)
    }

    println(sum)
  }

  fun getCharPriority(char: Char): Int {
    return if (char.isLowerCase()) {
      char.code - 96
    } else {
      char.code - 64 + 26
    }
  }
}

fun main() {
  RucksackPriorityPart2().calculatePriority()
}