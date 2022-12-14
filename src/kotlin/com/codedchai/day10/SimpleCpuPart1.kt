package com.codedchai.day10

import java.io.File

class SimpleCpuPart1 {
  var regX = 1
  var cycle = 1
  val capturedRegX = mutableListOf(regX)
  fun executeCommands() {
    val lines = File("resources/day10/input.txt").readLines()

    lines.forEach { instruction ->
      if (instruction.equals("noop", true)) {
        incrementAndSaveCycle()
      } else {
        incrementAndSaveCycle()
        incrementAndSaveCycle()
        regX += instruction.split(" ").last().toInt()
      }
    }

    val signalStrength = capturedRegX.mapIndexed { index, x ->
      if (0 == (index - 20) % 40) {
        println("X: $x * index: $index = ${x * index}")
        x * index
      } else {
        0
      }
    }.sum()

    println(signalStrength)
  }

  fun incrementAndSaveCycle() {
    cycle++
    capturedRegX.add(regX)
  }
}

fun main() {
  SimpleCpuPart1().executeCommands()
}