package com.codedchai.day06

import java.io.File

class TuningTrouble {

  fun findStartOfPacketPosition(markerSize: Int): Int {
    val message = File("resources/day06/input.txt").readLines().first()
    return message.windowed(markerSize, 1)
      .indexOfFirst { section -> section.toSet().size == markerSize } + markerSize
  }
}

fun main() {
  TuningTrouble().findStartOfPacketPosition(4).also { println("Part 1 answer: $it") }
  TuningTrouble().findStartOfPacketPosition(14).also { println("Part 2 answer: $it") }
}