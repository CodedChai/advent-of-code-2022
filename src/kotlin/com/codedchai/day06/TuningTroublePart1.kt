package com.codedchai.day06

import java.io.File

class TuningTroublePart1 {

  fun findStartOfPacketPosition() {
    val message = File("resources/day06/input.txt").readLines().first()
    val firstPositionToRead = message.windowed(4, 1)
      .indexOfFirst { section -> section.toSet().size == 4 } + 4

    println(firstPositionToRead)
  }
}

fun main() {
  TuningTroublePart1().findStartOfPacketPosition()
}