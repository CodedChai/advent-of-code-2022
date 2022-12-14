package com.codedchai.day09

import java.io.File
import kotlin.math.abs

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

data class Coordinates(
  val x: Int,
  val y: Int
) {

  fun moveTowards(other: Coordinates): Coordinates {
    return if (x != other.x && y != other.y) {
      moveDiagonallyTowards(other)
    } else if (x < other.x) {
      moveRight()
    } else if (x > other.x) {
      moveLeft()
    } else if (y < other.y) {
      moveUp()
    } else if (y > other.y) {
      moveDown()
    } else {
      this
    }
  }

  fun moveDiagonallyTowards(other: Coordinates): Coordinates {
    return if (x < other.x && y < other.y) {
      moveRight().moveUp()
    } else if (x > other.x && y < other.y) {
      moveLeft().moveUp()
    } else if (x < other.x && y > other.y) {
      moveRight().moveDown()
    } else if (x > other.x && y > other.y) {
      moveLeft().moveDown()
    } else {
      this
    }
  }

  fun isTouching(other: Coordinates): Boolean {
    return abs(x - other.x) <= 1 && abs(y - other.y) <= 1
  }

  fun moveRight(): Coordinates {
    return copy(x = x + 1)
  }

  fun moveLeft(): Coordinates {
    return copy(x = x - 1)
  }

  fun moveUp(): Coordinates {
    return copy(y = y + 1)
  }

  fun moveDown(): Coordinates {
    return copy(y = y - 1)
  }
}

fun main() {
  RopeBridgePart1().calculateUniqueTailPositions()
}