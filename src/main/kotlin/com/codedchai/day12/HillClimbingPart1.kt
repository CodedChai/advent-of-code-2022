package com.codedchai.day12

import java.io.File

class HillClimbingPart1 {

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
    val num = searchBfsBacktracking(grid)
    println(num)
  }

  fun getStartingCoordinates(grid: List<List<Int>>, startingChar: Char): Coordinates {
    val outerPos = grid.indexOfFirst { line ->
      line.contains(startingChar.code)
    }
    val innerPos = grid[outerPos].indexOf(startingChar.code)
    return Coordinates(outerPos, innerPos)
  }

  fun List<List<Int>>.valueOf(coordinates: Coordinates): Int {
    return this[coordinates.rowPos][coordinates.colPos]
  }


  fun searchBfsBacktracking(grid: List<List<Int>>): Int {
    val startingCoordinates = getStartingCoordinates(grid, 'S')
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
      throw Error("No goal found")
    }

    val path = mutableListOf<Node>(reachedGoal)

    while (path.last().parent != null) {
      path.add(path.last().parent!!)
    }
    return path.size - 1 // we don't count the last place because we just want how many steps it took
  }

  fun searchBfs(grid: List<List<Int>>) {
    val startingCoordinates = getStartingCoordinates(grid, 'S')
    val (maxCol, maxRow) = Pair(grid[0].size, grid.size)
    val startingValue = grid.valueOf(startingCoordinates)
    val node = Node(startingCoordinates, startingValue)
    val queue = mutableListOf(node)
    val visited = hashSetOf<Coordinates>()
    val testList = mutableListOf(mutableListOf(node))

    while (queue.isNotEmpty()) {
      val (currCoords, currValue) = queue.removeFirst()
      visited.add(currCoords)
      println("Value: ${currValue.toChar()} @ $currCoords")

      val validNeighbors = currCoords.buildNeighbors(maxCol, maxRow).filter {
        canMoveTo(currValue, grid.valueOf(it)) && visited.add(it)
      }.map { Node(it, grid.valueOf(it)) }

      val reachedGoal = validNeighbors.find {
        it.value == 'E'.code && canMoveTo(currValue, it.value)
      }
      if (reachedGoal != null) {
        println(reachedGoal)
        println(visited.size)
        println(queue.size)
        break
      }
      queue.addAll(validNeighbors)
      queue.sortByDescending { it.value } // Prioritize highest values first
    }

  }

  data class PathLocation(
    val path: List<Node>,
    val visited: Set<Coordinates> = hashSetOf()
  )

  fun searchBfs2(grid: List<List<Int>>) {
    val startingCoordinates = getStartingCoordinates(grid, 'S')
    val (maxCol, maxRow) = Pair(grid[0].size, grid.size)
    val startingValue = grid.valueOf(startingCoordinates)
    val node = Node(startingCoordinates, startingValue)
    val queue = mutableListOf(PathLocation(mutableListOf(node)))

    val viablePaths = mutableListOf<PathLocation>()
    while (queue.isNotEmpty()) {
      val currentPath = queue.removeFirst()
      val (currCoords, currValue) = currentPath.path.last()
      val visited = currentPath.visited
      // println("Value: ${currValue.toChar()} @ $currCoords")

      val validNeighbors = currCoords.buildNeighbors(maxCol, maxRow).filter {
        canMoveTo(currValue, grid.valueOf(it)) && !visited.contains(it)
      }.map { Node(it, grid.valueOf(it)) }

      val reachedGoal = validNeighbors.find {
        it.value == 'E'.code && canMoveTo(currValue, it.value)
      }
      if (reachedGoal != null) {
        println(reachedGoal)
        println(visited.size)
        println(queue.size)
        viablePaths.add(currentPath)
      }
      val newPaths = validNeighbors.map {
        currentPath.copy(path = currentPath.path + it, visited = currentPath.visited + it.coordinates)
      }.filter { newPath ->
        queue.none { queuedPath ->
          newPath.path == queuedPath.path // filter out existing combinations
        } && viablePaths.none { viablePath ->
          viablePath.path == newPath.path
        }
      }

      queue.addAll(newPaths)

      // queue.sortByDescending { it.path.last().value } // Prioritize highest values first
    }

    viablePaths.sortByDescending { it.path.size }
    viablePaths.forEach {
      println("Viable path of length: ${it.path.size}")
    }
  }

  fun search(grid: List<List<Int>>): Set<Coordinates> {
    val visited = hashSetOf<Coordinates>()
    val requiredCoords = hashSetOf<Coordinates>()
    fun search(currentCoords: Coordinates, lastValue: Int? = null): Boolean {
      val currValue = grid[currentCoords.rowPos][currentCoords.colPos]
      if (!canMoveTo(lastValue, currValue)) {
        return false
      }
      if (!visited.add(currentCoords)) {
        return false
      } else {
        requiredCoords.add(currentCoords)
      }
      println(currValue.toChar())
      val neighboringCoords = currentCoords.buildNeighbors(grid[0].size, grid.size)
      val reachedGoalCoords = neighboringCoords.find { grid[it.rowPos][it.colPos] == 'E'.code }
      if (reachedGoalCoords != null && canMoveTo(lastValue, grid[reachedGoalCoords.rowPos][reachedGoalCoords.colPos])) {
        return true
      }
      val reachedGoal = neighboringCoords.any {
        search(it, currValue)
      }
      if (!reachedGoal) {
        requiredCoords.remove(currentCoords)
      }
      return reachedGoal
    }

    val outerPos = grid.indexOfFirst { line ->
      line.contains('S'.code)
    }
    val innerPos = grid[outerPos].indexOf('S'.code)
    search(Coordinates(outerPos, innerPos))
    return requiredCoords
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
  HillClimbingPart1().solve()
}