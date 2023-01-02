package com.codedchai.day17

import com.codedchai.day12.Coordinates
import java.io.File

class FlowPart1 {
  fun simulatePyroclasticFlow() {
    val jetDirections = File("resources/day17/input.txt").readLines().first().map { it.toJetDirection() }

    val finalRestingPlaceRocks = mutableListOf<RockShape>()

    println(jetDirections)
    var jetDirectionIndex = 0
    while (finalRestingPlaceRocks.size < 2022) {
      var rockMovementNumber = 0
      val minimumHeight = getHeight(finalRestingPlaceRocks) + 3
      var currentRock = buildRockShape(finalRestingPlaceRocks.size, minimumHeight)

      while (true) {
        val directionToMove = if (rockMovementNumber % 2 == 0) {
          jetDirections[jetDirectionIndex].also {
            jetDirectionIndex = (jetDirectionIndex + 1) % jetDirections.size
          }
        } else {
          Direction.DOWN
        }
        rockMovementNumber++
        val newRockPosition = if (Direction.DOWN == directionToMove) {
          newRockPosition(currentRock, directionToMove, finalRestingPlaceRocks)
        } else {
          newRockPosition(currentRock, directionToMove, finalRestingPlaceRocks)
        }
        if (newRockPosition != null) { // Only update if it is a valid movement
          currentRock = newRockPosition
        } else if (directionToMove == Direction.DOWN) { // Only end if we are going down
          break
        }
      }
      finalRestingPlaceRocks.add(currentRock)
    }
    val height = getHeight(finalRestingPlaceRocks)

    visualizeRocks(finalRestingPlaceRocks)
    println("Max height: $height")
  }

  fun visualizeRocks(finalRocks: List<RockShape>) {
    val maxHeight = getHeight(finalRocks)
    val finalPoints = finalRocks.flatMap { it.points }.toHashSet()
    for (y in maxHeight downTo 0) {
      print(y.toString().padStart(4, '0') + ": ")
      for (x in 0..6) {
        val coordToDraw = Coordinates(y, x)
        if (coordToDraw in finalPoints) {
          print("#")
        } else {
          print(".")
        }
      }
      println()
    }
  }

  fun getHeight(restingRocks: List<RockShape>): Int {
    if (restingRocks.isEmpty()) {
      return 0
    }
    return restingRocks.flatMap { it.points.map { it.rowPos } }.max() + 1
  }

  fun buildRockShape(currentSize: Int, minimumHeight: Int): RockShape {
    val rockBuilderFunctions = listOf(::buildHorizontalShapeOne, ::buildPlusShapeTwo, ::buildBackwardsLShapeThree, ::buildVerticalShapeFour, ::buildSquareShapeFive)
    val currentFunIndex = currentSize % rockBuilderFunctions.size
    return rockBuilderFunctions[currentFunIndex](minimumHeight)
  }

  fun Char.toJetDirection(): Direction {
    return when (this) {
      '>' -> Direction.RIGHT
      '<' -> Direction.LEFT
      else -> throw Error("Invalid input char of: $this")
    }
  }

  fun isAtRestingSpot(possiblePosition: RockShape, restingRocks: List<RockShape>): Boolean {
    if (possiblePosition.points.any { it.rowPos < 0 }) {
      return true
    }
    return possiblePosition.points.any { point -> point in restingRocks.flatMap { restingRock -> restingRock.points } }
  }

  fun newRockPosition(currentRock: RockShape, direction: Direction, restingRocks: List<RockShape>): RockShape? {
    val possibleRockPosition = currentRock.move(direction)
    return when (isAtRestingSpot(possibleRockPosition, restingRocks)) {
      true -> null
      false -> possibleRockPosition
    }
  }

  /**
   * it will look like ####
   * must be 3 above the floor/highest shape - should already be calculated to have the +3
   * must be 2 from the left edge
   */
  fun buildHorizontalShapeOne(minimumHeight: Int): RockShape {
    val points = listOf(Coordinates(minimumHeight, 2), Coordinates(minimumHeight, 3), Coordinates(minimumHeight, 4), Coordinates(minimumHeight, 5))
    return RockShape(points)
  }

  /**
   * it will look like
   *
   * .#.
   * ###
   * .#.
   *
   * must be 3 above the floor/highest shape - should already be calculated to have the +3
   * must be 2 from the left edge
   */
  fun buildPlusShapeTwo(minimumHeight: Int): RockShape {
    val points = listOf(
      Coordinates(minimumHeight, 3), // bottom point
      Coordinates(minimumHeight + 1, 2), Coordinates(minimumHeight + 1, 3), Coordinates(minimumHeight + 1, 4), // middle row
      Coordinates(minimumHeight + 2, 3) // top point
    )
    return RockShape(points)
  }

  /**
   * it will look like
   *
   * ..#
   * ..#
   * ###
   *
   * must be 3 above the floor/highest shape - should already be calculated to have the +3
   * must be 2 from the left edge
   */
  fun buildBackwardsLShapeThree(minimumHeight: Int): RockShape {
    val points = listOf(
      Coordinates(minimumHeight, 2), Coordinates(minimumHeight, 3), Coordinates(minimumHeight, 4), // bottom row
      Coordinates(minimumHeight + 1, 4), // middle point
      Coordinates(minimumHeight + 2, 4) // top point
    )
    return RockShape(points)
  }

  /**
   * it will look like
   *
   * #
   * #
   * #
   * #
   *
   * must be 3 above the floor/highest shape - should already be calculated to have the +3
   * must be 2 from the left edge
   */
  fun buildVerticalShapeFour(minimumHeight: Int): RockShape {
    val points = listOf(
      Coordinates(minimumHeight, 2),
      Coordinates(minimumHeight + 1, 2),
      Coordinates(minimumHeight + 2, 2),
      Coordinates(minimumHeight + 3, 2),
    )
    return RockShape(points)
  }

  /**
   * it will look like
   *
   * ##
   * ##
   *
   * must be 3 above the floor/highest shape - should already be calculated to have the +3
   * must be 2 from the left edge
   */
  fun buildSquareShapeFive(minimumHeight: Int): RockShape {
    val points = listOf(
      Coordinates(minimumHeight, 2), Coordinates(minimumHeight, 3), // bottom row
      Coordinates(minimumHeight + 1, 2), Coordinates(minimumHeight + 1, 3), // top row
    )
    return RockShape(points)
  }
}

fun main() {
  FlowPart1().simulatePyroclasticFlow()
}

enum class Direction {
  LEFT,
  RIGHT,
  DOWN
}

fun clamp(value: Int, min: Int, max: Int): Int {
  return maxOf(min, value).let { clampedLowerBound ->
    minOf(clampedLowerBound, max)
  }
}

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