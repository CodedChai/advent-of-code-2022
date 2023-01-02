package com.codedchai.day17

import com.codedchai.day12.Coordinates
import java.io.File

class FlowPart2 {
  fun simulatePyroclasticFlow() {
    val jetDirections = File("resources/day17/input.txt").readLines().first().map { it.toJetDirection() }

    val finalRestingPlaceRockPoints = hashSetOf<Coordinates>()

    var jetDirectionIndex = 0
    var numberOfFinalRestingPoints = 0L
    while (numberOfFinalRestingPoints < 2022) {
      var rockMovementNumber = 0
      val minimumHeight = getHeight(finalRestingPlaceRockPoints) + 3
      var currentRock = buildRockShape(numberOfFinalRestingPoints, minimumHeight)

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
          newRockPosition(currentRock, directionToMove, finalRestingPlaceRockPoints)
        } else {
          newRockPosition(currentRock, directionToMove, finalRestingPlaceRockPoints)
        }
        if (newRockPosition != null) { // Only update if it is a valid movement
          currentRock = newRockPosition
        } else if (directionToMove == Direction.DOWN) { // Only end if we are going down
          break
        }
      }
      finalRestingPlaceRockPoints.addAll(currentRock.points)
      numberOfFinalRestingPoints++
    }
    val height = getHeight(finalRestingPlaceRockPoints)

    visualizeRocks(finalRestingPlaceRockPoints)
    println("Max height: $height")
  }

  fun visualizeRocks(finalPoints: Set<Coordinates>) {
    val maxHeight = getHeight(finalPoints)
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

  fun getHeight(restingRockPoints: Set<Coordinates>): Int {
    if (restingRockPoints.isEmpty()) {
      return 0
    }
    return restingRockPoints.maxOf { it.rowPos } + 1
  }

  fun buildRockShape(currentSize: Long, minimumHeight: Int): RockShape {
    val rockBuilderFunctions = listOf(::buildHorizontalShapeOne, ::buildPlusShapeTwo, ::buildBackwardsLShapeThree, ::buildVerticalShapeFour, ::buildSquareShapeFive)
    val currentFunIndex = (currentSize % rockBuilderFunctions.size).toInt()
    return rockBuilderFunctions[currentFunIndex](minimumHeight)
  }

  fun Char.toJetDirection(): Direction {
    return when (this) {
      '>' -> Direction.RIGHT
      '<' -> Direction.LEFT
      else -> throw Error("Invalid input char of: $this")
    }
  }

  fun isAtRestingSpot(possiblePosition: RockShape, restingRocks: Set<Coordinates>): Boolean {
    if (possiblePosition.points.any { it.rowPos < 0 }) {
      return true
    }
    return possiblePosition.points.any { point -> point in restingRocks }
  }

  fun newRockPosition(currentRock: RockShape, direction: Direction, restingRocks: Set<Coordinates>): RockShape? {
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
  FlowPart2().simulatePyroclasticFlow()
}

data class Key(
  val pieceCount: Int,
  val jetCount: Int
)

data class CachedValue(
  val timesSeen: Int,
  val piecesSeen: Long,
  val currentTop: Long
)