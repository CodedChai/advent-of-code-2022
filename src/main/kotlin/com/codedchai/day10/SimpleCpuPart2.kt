package com.codedchai.day10

import java.io.File
import kotlin.math.abs

class SimpleCpuPart2 {
  var regX = 1
  var cycle = 0
  val capturedRegX = mutableMapOf(cycle to regX)
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
    drawCrt()
  }

  fun drawCrt() {
    (0..5).forEach { row ->
      println()
      (0..39).forEach { pixel ->
        val cycleToCheck = pixel + (row * 40) + 1
        if (abs(capturedRegX[cycleToCheck]!! - pixel) <= 1) {
          print("#")
        } else {
          print(".")
        }
        print(" ")
      }
    }
  }

  fun incrementAndSaveCycle() {
    cycle++
    capturedRegX[cycle] = regX
  }
}

fun main() {
  SimpleCpuPart2().executeCommands()
}