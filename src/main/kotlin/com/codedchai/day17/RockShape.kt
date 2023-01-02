package com.codedchai.day17

import com.codedchai.day12.Coordinates

data class RockShape(
  val points: List<Coordinates>
) {
  fun move(direction: Direction): RockShape {
    return when (direction) {
      Direction.LEFT -> move(0, -1)
      Direction.RIGHT -> move(0, 1)
      Direction.DOWN -> move(-1, 0)
    }
  }

  private fun move(verticalOffset: Int, horizontalOffset: Int): RockShape {

    val newPoints = points.map { point ->
      point.copy(
        rowPos = point.rowPos + verticalOffset,
        colPos = point.colPos + horizontalOffset
      )
    }
    return if (newPoints.any { it.colPos < 0 || it.colPos > 6 }) { // If the movement is illegal
      this
    } else {
      copy(points = newPoints)
    }
  }
}