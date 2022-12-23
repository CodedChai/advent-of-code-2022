package com.codedchai.day04

import java.io.File

class CampCleanupOverlapPart2 {
  fun getNumberOfOverlap() {
    val lines = File("resources/day04/input.txt").readLines()
    val numberOfOverlaps = lines.mapNotNull { line ->
      val (firstRange, secondRange) = line.split(',').map { it.toRange() }

      if (firstRange.any { it in secondRange } || secondRange.any { it in firstRange }) {
        line
      } else {
        null
      }
    }.size
    println(numberOfOverlaps)
  }

  fun String.toRange(): Set<Int> {
    val (firstNumber, secondNumber) = this.split('-')
    return (firstNumber.toInt()..secondNumber.toInt()).toHashSet()
  }
}

fun main() {
  CampCleanupOverlapPart2().getNumberOfOverlap()
}