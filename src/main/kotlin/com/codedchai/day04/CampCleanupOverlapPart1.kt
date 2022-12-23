package com.codedchai.day04

import java.io.File

class CampCleanupOverlapPart1 {
  fun getNumberOfOverlap() {
    val lines = File("resources/day04/input.txt").readLines()
    val numberOfOverlaps = lines.mapNotNull { line ->
      val (firstRange, secondRange) = line.split(',').map { it.toRange() }

      if (firstRange.all { it in secondRange } || secondRange.all { it in firstRange }) {
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
  CampCleanupOverlapPart1().getNumberOfOverlap()
}