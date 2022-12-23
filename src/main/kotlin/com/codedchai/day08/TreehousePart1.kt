package com.codedchai.day08

import java.io.File

class TreehousePart1 {
  fun buildGrid() {
    val grid = File("resources/day08/input.txt").readLines().map { line ->
      line.toCharArray().map { it.digitToInt() }
    }

    val heightInfoMap = mutableMapOf<Coordinates, Int>()

    var numInternallyVisible = 0
    for (row in 1 until grid.size - 1) {
      for (column in 1 until grid[row].size - 1) {
        val isVisible = Direction.values().any { direction ->
          val maxHeightInfo = getMaxHeightForDirection(row, column, grid, heightInfoMap, direction)
          maxHeightInfo < grid[row][column]
        }
        if (isVisible) {
          numInternallyVisible++
        }
      }
    }
    println("Num internally visible: $numInternallyVisible")
    val numExternallyVisible = grid.size * 2 + grid[0].size * 2 - 4
    println("Num externally visible: $numExternallyVisible")
    val totalVisible = numInternallyVisible + numExternallyVisible
    println("Total visible: $totalVisible")
  }

  fun getNextCoordsForDir(row: Int, col: Int, direction: Direction): Pair<Int, Int> {
    return when (direction) {
      Direction.FROM_LEFT -> Pair(row, col - 1)
      Direction.FROM_RIGHT -> Pair(row, col + 1)
      Direction.FROM_BOTTOM -> Pair(row + 1, col)
      Direction.FROM_TOP -> Pair(row - 1, col)
    }
  }

  fun getMaxHeightForDirection(row: Int, col: Int, grid: List<List<Int>>, heightMapInfo: MutableMap<Coordinates, Int>, direction: Direction): Int {
    val (newRowPos, newColPos) = getNextCoordsForDir(row, col, direction)
    val coordinates = Coordinates(newRowPos, newColPos, direction)
    val cachedMaxHeight = heightMapInfo[coordinates]

    if (coordinates.colPos <= 0 || coordinates.rowPos <= 0 || coordinates.rowPos >= grid.size - 1 || coordinates.colPos >= grid[row].size - 1) {
      return grid[coordinates.rowPos][coordinates.colPos]
    }

    if (cachedMaxHeight == null) {

      heightMapInfo[coordinates] = maxOf(grid[coordinates.rowPos][coordinates.colPos], getMaxHeightForDirection(coordinates.rowPos, coordinates.colPos, grid, heightMapInfo, direction))
    }

    return heightMapInfo[coordinates]!!
  }
}

fun main() {
  TreehousePart1().buildGrid()
}