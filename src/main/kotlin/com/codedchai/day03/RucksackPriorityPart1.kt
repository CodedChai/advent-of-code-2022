package com.codedchai.day03

import java.io.File

class RucksackPriorityPart1 {
  fun calculatePriority() {
    var sum = 0

    File("resources/day03/input.txt").forEachLine { line ->
      val firstCompartment = line.slice(0 until line.length / 2).toHashSet()
      val secondCompartment = line.slice(line.length / 2 until line.length)
      val duplicateChar = secondCompartment.first { it in firstCompartment }
      sum += getCharPriority(duplicateChar)
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
  RucksackPriorityPart1().calculatePriority()
}