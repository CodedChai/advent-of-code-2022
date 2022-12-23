package com.codedchai.day09

import java.io.File

class RopeBridgePart1 {
  fun calculateUniqueTailPositions() {
    val lines = File("resources/day09/input.txt").readLines()
    var head = Coordinates(0, 0)
    var tail = Coordinates(0, 0)
    val uniqueTailLocations = hashSetOf(tail)

    lines.forEach { line ->
      println(line)
      println("------------------")
      val numToMove = numberToMove(line)
      repeat(numToMove) {
        head = moveOne(head, line)
        tail = moveTailToHead(head, tail)
        println("Head: $head")
        println("Tail: $tail")
        uniqueTailLocations.add(tail)
      }
    }

    println(uniqueTailLocations.size)
  }

  fun moveTailToHead(head: Coordinates, tail: Coordinates): Coordinates {
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
  RopeBridgePart1().calculateUniqueTailPositions()
}