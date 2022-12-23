package com.codedchai.day14

import com.codedchai.day12.Coordinates
import java.io.File

class ReservoirPart2 {

  fun process(heightFromFloor: Int) {
    val textLines = File("resources/day14/input.txt").readLines()

    val rockLocations = textLines.flatMap { textLine ->
      val pointPairs = getPointPairs(textLine)
      pointPairs.flatMap {
        createLinePoints(it.first, it.second)
      }
    }

    val maxY = rockLocations.maxOf { it.rowPos } + heightFromFloor
    var minX = rockLocations.minOf { it.colPos }
    var maxX = rockLocations.maxOf { it.colPos }
    val tileGrid = initializeTileGrid(maxY, maxX * 2) // just make the grid really big

    rockLocations.forEach {
      tileGrid[it.rowPos][it.colPos] = Tile.ROCK
    }

    if (heightFromFloor > 0) {
      tileGrid[maxY].indices.forEach { xPos ->
        tileGrid[maxY][xPos] = Tile.ROCK
      }
    }

    val sandStartingPosition = Coordinates(0, 500)

    var totalSandParticles = 0
    while (true) {
      val restingSpot = findSandRestingSpot(sandStartingPosition, tileGrid, maxY)
      restingSpot?.also {
        tileGrid[restingSpot.rowPos][restingSpot.colPos] = Tile.SAND
        minX = minOf(restingSpot.colPos, minX)
        maxX = maxOf(restingSpot.colPos, maxX)
        totalSandParticles++
      }

      if (restingSpot == sandStartingPosition) {
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

fun main() {
  ReservoirPart2().process(2)
}