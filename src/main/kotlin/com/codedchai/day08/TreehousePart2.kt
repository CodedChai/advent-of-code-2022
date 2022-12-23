package com.codedchai.day08

import java.io.File

class TreehousePart2 {
  fun buildGrid() {
    val grid = File("resources/day08/input.txt").readLines().map { line ->
      line.toCharArray().map { it.digitToInt() }
    }

    val heightInfoMap = mutableMapOf<Coordinates, Int>()

    var maxTreesSeen = 0
    for (row in 1 until grid.size - 1) {
      for (column in 1 until grid[row].size - 1) {
        val treesSeen = Direction.values().map { direction ->
          getScoreForDirection(grid[row][column], row, column, grid, direction)
        }
        val scenicScore = treesSeen.reduceRight { i, acc -> i * acc }
        maxTreesSeen = maxOf(scenicScore, maxTreesSeen)
      }
    }

    println(maxTreesSeen)
  }

  fun getNextCoordsForDir(row: Int, col: Int, direction: Direction): Pair<Int, Int> {
    return when (direction) {
      Direction.FROM_LEFT -> Pair(row, col - 1)
      Direction.FROM_RIGHT -> Pair(row, col + 1)
      Direction.FROM_BOTTOM -> Pair(row + 1, col)
      Direction.FROM_TOP -> Pair(row - 1, col)
    }
  }

  fun getScoreForDirection(treeHeight: Int, row: Int, col: Int, grid: List<List<Int>>, direction: Direction): Int {
    val (newRowPos, newColPos) = getNextCoordsForDir(row, col, direction)
    val coordinates = Coordinates(newRowPos, newColPos, direction)
    if (coordinates.colPos < 0 || coordinates.rowPos < 0 || coordinates.rowPos > grid.size - 1 || coordinates.colPos > grid[row].size - 1) {
      return 0
    }

    val newTreeHeight = grid[coordinates.rowPos][coordinates.colPos]

    return if (newTreeHeight == treeHeight) {
      1
    } else if (newTreeHeight < treeHeight) {
      1 + getScoreForDirection(treeHeight, coordinates.rowPos, coordinates.colPos, grid, direction)
    } else {
      0
    }
  }
}

fun main() {
  TreehousePart2().buildGrid()
}