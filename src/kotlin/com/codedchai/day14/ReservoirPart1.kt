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

    val maxY = rockLocations.maxOf { it.rowPos }
    val minX = rockLocations.minOf { it.colPos }
    val maxX = rockLocations.maxOf { it.colPos }
    val tileGrid = initializeTileGrid(maxY, maxX)

    rockLocations.forEach {
      tileGrid[it.rowPos][it.colPos] = Tile.ROCK
    }

    val sandStartingPosition = Coordinates(0, 500)

    var totalSandParticles = 0
    while (true) {
      val restingSpot = findSandRestingSpot(sandStartingPosition, tileGrid, maxY)
      restingSpot?.also {
        tileGrid[restingSpot.rowPos][restingSpot.colPos] = Tile.SAND
        totalSandParticles++
      }

      if (restingSpot == sandStartingPosition || restingSpot == null) {
        break
      }
    }

    (0..maxY).forEach { y ->
      println()
      (minX..maxX).forEach { x ->
        print(tileGrid[y][x].char)
      }
    }
    println()

    println("Sand particles: $totalSandParticles")
  }

  fun findSandRestingSpot(sandPosition: Coordinates, tileGrid: List<List<Tile>>, maxY: Int): Coordinates? {
    if (maxY <= sandPosition.rowPos) {
      return null
    }

    val canFallDown = tileGrid[sandPosition.rowPos + 1][sandPosition.colPos] == Tile.AIR
    val canFallDownLeft = tileGrid[sandPosition.rowPos + 1][sandPosition.colPos - 1] == Tile.AIR
    val canFallDownRight = tileGrid[sandPosition.rowPos + 1][sandPosition.colPos + 1] == Tile.AIR

    return when {
      canFallDown -> findSandRestingSpot(sandPosition.copy(rowPos = sandPosition.rowPos + 1), tileGrid, maxY)
      canFallDownLeft -> findSandRestingSpot(sandPosition.copy(rowPos = sandPosition.rowPos + 1, colPos = sandPosition.colPos - 1), tileGrid, maxY)
      canFallDownRight -> findSandRestingSpot(sandPosition.copy(rowPos = sandPosition.rowPos + 1, colPos = sandPosition.colPos + 1), tileGrid, maxY)

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

  private fun initializeTileGrid(maxY: Int, maxX: Int) = (0..maxY).map { y ->
    (0..maxX).map { x ->
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