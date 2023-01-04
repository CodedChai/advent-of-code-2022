package com.codedchai.day18

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File

class BouldersPart1 {
  suspend fun calculateSurfaceArea() = coroutineScope {
    val points = File("resources/day18/input.txt").readLines().map { it.buildPoint() }.toHashSet()
    points.forEach { println(it) }

    val surfaceAreaEstimate = points.map { point ->
      async {
        val touchingNeighborsCount = point.neighboringPoints().filter { neighbor ->
          neighbor in points
        }.size
        6 - touchingNeighborsCount
      }
    }.awaitAll().sum()

    println(surfaceAreaEstimate)
  }

  fun String.buildPoint(): ThreeDPoint {
    val (x, y, z) = split(",").map { it.toInt() }
    return ThreeDPoint(x, y, z)
  }

}

data class ThreeDPoint(
  val x: Int,
  val y: Int,
  val z: Int
) {
  fun neighboringPoints(): List<ThreeDPoint> {
    return listOf(
      copy(x = x - 1),
      copy(x = x + 1),
      copy(y = y - 1),
      copy(y = y + 1),
      copy(z = z - 1),
      copy(z = z + 1),
    )
  }
}

suspend fun main() {
  BouldersPart1().calculateSurfaceArea()
}