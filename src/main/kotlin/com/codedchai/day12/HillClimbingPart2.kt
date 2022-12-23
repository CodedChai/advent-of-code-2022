package com.codedchai.day12

import java.io.File

class HillClimbingPart2 {

  fun solve() {
    val grid = File("resources/day12/input.txt").readLines().map { line ->
      line.toCharArray().map { it.code }
    }

    grid.forEach {
      println(it)
    }

//    val visits = search(grid)
//
//    println(visits)
//    println(visits.size)

    // searchBfs2(grid)
    val startingCoordinates = getStartingCoordinates(grid, 'a')

    val possibleSteps = startingCoordinates.map { startingPosition ->
      searchBfsBacktracking(grid, startingPosition)
    }
    println(possibleSteps)
    println("answer: ${possibleSteps.min()}")
  }

  fun getStartingCoordinates(grid: List<List<Int>>, startingChar: Char): List<Coordinates> {
    val startingCoordinates = mutableListOf<Coordinates>()
    for (i in grid.indices) {
      for (j in grid.indices) {
        if (grid[i][j] == startingChar.code) {
          startingCoordinates.add((Coordinates(i, j)))
        }
      }
    }

    return startingCoordinates
  }

  fun List<List<Int>>.valueOf(coordinates: Coordinates): Int {
    return this[coordinates.rowPos][coordinates.colPos]
  }


  fun searchBfsBacktracking(grid: List<List<Int>>, startingCoordinates: Coordinates): Int {
    val (maxCol, maxRow) = Pair(grid[0].size, grid.size)
    val startingValue = grid.valueOf(startingCoordinates)
    val node = Node(startingCoordinates, startingValue)
    val queue = mutableListOf(node)
    val visited = hashSetOf<Coordinates>()
    var reachedGoal: Node? = null
    while (queue.isNotEmpty()) {
      val node = queue.removeFirst()
      val (currCoords, currValue) = node
      visited.add(currCoords)
      println("Value: ${currValue.toChar()} @ $currCoords")

      val validNeighbors = currCoords.buildNeighbors(maxCol, maxRow).filter {
        canMoveTo(currValue, grid.valueOf(it)) && visited.add(it)
      }.map { Node(it, grid.valueOf(it), node) }

      reachedGoal = validNeighbors.find {
        it.value == 'E'.code && canMoveTo(currValue, it.value)
      }
      if (reachedGoal != null) {
        println(reachedGoal)
        println(visited.size)
        println(queue.size)
        break
      }
      queue.addAll(validNeighbors)
    }

    if (reachedGoal == null) {
      return Int.MAX_VALUE
    }

    val path = mutableListOf(reachedGoal)

    while (path.last().parent != null) {
      path.add(path.last().parent!!)
    }
    return path.size - 1 // we don't count the last place because we just want how many steps it took
  }

  fun Coordinates.buildNeighbors(maxCol: Int, maxRow: Int): List<Coordinates> {
    return ((-1..1 step 2).map { xDiff ->
      this.copy(colPos = this.colPos + xDiff)
    } + (-1..1 step 2).map { yDiff ->
      this.copy(rowPos = this.rowPos + yDiff)
    }).filter { it.rowPos >= 0 && it.colPos >= 0 && it.colPos < maxCol && it.rowPos < maxRow }
  }

  fun canMoveTo(lastValue: Int?, newValue: Int): Boolean {
    lastValue ?: return true
    return if (newValue == 'E'.code) {
      lastValue >= 'z'.code - 1
    } else if (lastValue == 'S'.code) {
      'a'.code >= newValue - 1
    } else {
      lastValue >= newValue - 1
    }
  }
}

fun main() {
  HillClimbingPart2().solve()
}