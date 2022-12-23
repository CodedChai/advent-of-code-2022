package com.codedchai.day09

import kotlin.math.abs

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
