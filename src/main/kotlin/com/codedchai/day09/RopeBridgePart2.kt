package com.codedchai.day09

import java.io.File

class RopeBridgePart2 {
  fun calculateUniqueTailPositions() {
    val lines = File("resources/day09/input.txt").readLines()
    val rope = (0..9).map { Coordinates(0, 0) }.toMutableList()
    val uniqueTailLocations = hashSetOf(rope.last())

    lines.forEach { line ->
      println(line)
      println("------------------")
      val numToMove = numberToMove(line)
      repeat(numToMove) {
        for (index in 0 until rope.size) {
          if (index == 0) {
            rope[0] = moveOne(rope.first(), line)
            continue
          }
          rope[index] = moveSecondKnotTowardsFirstKnot(rope[index - 1], rope[index])
          if (index == rope.size - 1) {
            uniqueTailLocations.add(rope[index])
          }
        }
      }
    }

    println(uniqueTailLocations.size)
  }

  fun moveSecondKnotTowardsFirstKnot(head: Coordinates, tail: Coordinates): Coordinates {
    if (tail.isTouching(head)) {
      return tail
    }

    return tail.moveTowards(head)
  }

  fun numberToMove(line: String): Int {
    return line.split(" ").last().toInt()
  }

  fun moveOne(coordinates: Coordinates, line: String): Coordinates {
    val direction = line.split(" ").first()

    return when {
      direction.equals("R", true) -> coordinates.moveRight()
      direction.equals("L", true) -> coordinates.moveLeft()
      direction.equals("U", true) -> coordinates.moveUp()
      direction.equals("D", true) -> coordinates.moveDown()
      else -> throw RuntimeException("Failed to interpret line: $line when we are at position: $coordinates")
    }

  }
}

fun main() {
  RopeBridgePart2().calculateUniqueTailPositions()
}