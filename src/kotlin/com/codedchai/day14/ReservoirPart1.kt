package com.codedchai.day14

import com.codedchai.day12.Coordinates
import java.io.File

class ReservoirPart1 {

  fun process() {
    val textLines = File("resources/day14/input.txt").readLines()

    val rockLocations = textLines.flatMap { textLine ->
      val pointPairs = getPointPairs(textLine)
      pointPairs.flatMap {
        createLinePoints(it.first, it.second)
      }
    }
    val tileGrid = initializeTileGrid(1000)

    rockLocations.forEach {
      tileGrid[it.rowPos][it.colPos] = Tile.ROCK
    }

    val sandStartingPosition = Coordinates(0, 500)


    var totalSandParticles = 0
    while (true) {
      val restingSpot = findSandRestingSpot(sandStartingPosition, tileGrid)
      restingSpot?.also {
        tileGrid[restingSpot.rowPos][restingSpot.colPos] = Tile.SAND
        totalSandParticles++
      }

      if (restingSpot == sandStartingPosition || restingSpot == null) {
        break
      }
    }

    (0..9).forEach { y ->
      println()
      (494..503).forEach { x ->
        print(tileGrid[y][x].char)
      }
    }

    println("Sand particles: $totalSandParticles")
  }

  fun findSandRestingSpot(sandPosition: Coordinates, tileGrid: List<List<Tile>>): Coordinates? {

    // TODO: These checks need to be for if we are outside the bounds of all of our lines
    val canFallDown = if (tileGrid.size == sandPosition.rowPos + 1) {
      null
    } else {
      tileGrid[sandPosition.rowPos + 1][sandPosition.colPos] == Tile.AIR
    }
    val canFallDownLeft = if (tileGrid.size == sandPosition.rowPos + 1 || sandPosition.colPos - 1 < 0) {
      null
    } else {
      tileGrid[sandPosition.rowPos + 1][sandPosition.colPos - 1] == Tile.AIR
    }
    val canFallDownRight = if (tileGrid.size == sandPosition.rowPos + 1 || tileGrid[0].size == sandPosition.colPos + 1) {
      null
    } else {
      tileGrid[sandPosition.rowPos + 1][sandPosition.colPos + 1] == Tile.AIR
    }

    return when {
      canFallDown == true -> findSandRestingSpot(sandPosition.copy(rowPos = sandPosition.rowPos + 1), tileGrid)
      canFallDownLeft == true -> findSandRestingSpot(sandPosition.copy(rowPos = sandPosition.rowPos + 1, colPos = sandPosition.colPos - 1), tileGrid)
      canFallDownRight == true -> findSandRestingSpot(sandPosition.copy(rowPos = sandPosition.rowPos + 1, colPos = sandPosition.colPos + 1), tileGrid)
      canFallDown == null -> null
      canFallDownLeft == null -> null
      canFallDownRight == null -> null
      else -> sandPosition
    }
  }

  fun getPointPairs(textLine: String): List<Pair<Coordinates, Coordinates>> {
    return textLine.split(" -> ").windowed(2).map { pairOfPoints ->
      val (firstPointX, firstPointY) = pairOfPoints[0].split(",").map { position -> position.toInt() }
      val (secondPointX, secondPointY) = pairOfPoints[1].split(",").map { position -> position.toInt() }
      Coordinates(firstPointY, firstPointX) to Coordinates(secondPointY, secondPointX)
    }
  }

  fun createLinePoints(point1: Coordinates, point2: Coordinates): List<Coordinates> {
    val minX = minOf(point1.colPos, point2.colPos)
    val minY = minOf(point1.rowPos, point2.rowPos)
    val maxX = maxOf(point1.colPos, point2.colPos)
    val maxY = maxOf(point1.rowPos, point2.rowPos)

    return (minY..maxY).flatMap { rowPos ->
      (minX..maxX).map { colPos ->
        Coordinates(rowPos, colPos)
      }
    }
  }

  private fun initializeTileGrid(maxSize: Int) = (0..9).map { y ->
    (0..maxSize).map { x ->
      Tile.AIR
    }
  }.map { it.toMutableList() }.toMutableList()

}

enum class Tile(val char: Char) {
  AIR('.'),
  SAND('O'),
  ROCK('#')
}

fun main() {
  ReservoirPart1().process()
}