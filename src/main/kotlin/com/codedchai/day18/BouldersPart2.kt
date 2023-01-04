package com.codedchai.day18

import kotlinx.coroutines.coroutineScope
import java.io.File

class BouldersPart2 {
  suspend fun calculateSurfaceArea() = coroutineScope {
    val scannedPoints = File("resources/day18/input.txt").readLines().map { it.buildPoint() }.toHashSet()

    // Cushion the mins and maxes so we can have an outer layer to search with
    val minX = scannedPoints.minOf { it.x } - 1
    val minY = scannedPoints.minOf { it.y } - 1
    val minZ = scannedPoints.minOf { it.z } - 1
    val maxX = scannedPoints.maxOf { it.x } + 1
    val maxY = scannedPoints.maxOf { it.y } + 1
    val maxZ = scannedPoints.maxOf { it.z } + 1
    val minsAndMaxes = MinsAndMaxes(minX..maxX, minY..maxY, minZ..maxZ)

    println(minsAndMaxes)
    /* This also does not work. I tried looking at each point and since it said something about no diagonal movements
    just subtracting all the unique sides that got hit from inner air pockets by searching out in lines

    val airPockets = buildPointsToCheck(scannedPoints, minsAndMaxes)

    val pointsThatHaveInwardFacingSides = airPockets.map { airPocket ->
      getPointsThatHaveInwardFacingSides(airPocket, scannedPoints, minsAndMaxes)
    }.flatten().toHashSet()


    val surfaceAreaEstimate = scannedPoints.map { point ->
      async {
        val touchingNeighborsCount = point.neighboringPoints().filter { neighbor ->
          neighbor in scannedPoints
        }.size
        6 - touchingNeighborsCount
      }
    }.awaitAll().sum()

    println(surfaceAreaEstimate)

    println(pointsThatHaveInwardFacingSides)

    println(surfaceAreaEstimate - pointsThatHaveInwardFacingSides)
*/
    /* THIS IS WRONG, NOT WHAT IT IS ASKING FOR, I MISINTERPRETED THE QUESTION BUT IT DID GET SOMEONE ELSE'S ANSWER LOL
    val outerPoints = buildFront(minsAndMaxes) + buildBack(minsAndMaxes) + buildLeft(minsAndMaxes) + buildRight(minsAndMaxes) + buildTop(minsAndMaxes) + buildBottom(minsAndMaxes)

    val numberOfExteriorEdges = outerPoints.map { outerPoint ->
      async {
        hitsExteriorEdge(outerPoint, points, minsAndMaxes)
      }
    }.awaitAll().filter { it }.size

    println(numberOfExteriorEdges)
    */

    val answer = floodFill(scannedPoints, minsAndMaxes)

    println(answer)
  }

  fun floodFill(existingPoints: Set<ThreeDPoint>, minsAndMaxes: MinsAndMaxes): Int {
    val startingPoint = ThreeDPoint(minsAndMaxes.xRange.first, minsAndMaxes.yRange.first, minsAndMaxes.zRange.first)
    val stack = mutableListOf(startingPoint)
    var total = 0
    val visited = hashSetOf(startingPoint)
    while (stack.isNotEmpty()) {
      val currentPoint = stack.removeLast()

      //  println(currentPoint)

      if (currentPoint in existingPoints) {
        //  total++
        continue
      }

      val inBoundsNeighbors = currentPoint.neighboringPoints().filter { !isOutOfBounds(it, minsAndMaxes) }

      val (hitNeighbors, hitNothing) = inBoundsNeighbors.partition { it in existingPoints }

      total += hitNeighbors.size
      val unvisitedNeighbors = hitNothing.filter { visited.add(it) }
      stack.addAll(unvisitedNeighbors)
    }
    return total
  }

  tailrec fun hitsExteriorEdge(startingOuterPoint: PointAndMovementDirection, scannedPoints: Set<ThreeDPoint>, minsAndMaxes: MinsAndMaxes): Boolean {

    val currentPoint = startingOuterPoint.applyOffset()

    return if (isOutOfBounds(currentPoint.point, minsAndMaxes)) {
      false
    } else if (currentPoint.point in scannedPoints) {
      true
    } else {
      hitsExteriorEdge(currentPoint, scannedPoints, minsAndMaxes)
    }
  }


  fun buildFront(minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    return with(minsAndMaxes) {
      xRange.flatMap { x ->
        yRange.map { y ->
          PointAndMovementDirection(
            ThreeDPoint(x, y, zRange.first - 1),
            ThreeDPoint(z = 1)
          )
        }
      }
    }
  }

  fun buildBack(minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    return with(minsAndMaxes) {
      xRange.flatMap { x ->
        yRange.map { y ->
          PointAndMovementDirection(
            ThreeDPoint(x, y, zRange.last + 1),
            ThreeDPoint(z = -1)
          )
        }
      }
    }
  }


  fun buildLeft(minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    return with(minsAndMaxes) {
      zRange.flatMap { z ->
        yRange.map { y ->
          PointAndMovementDirection(
            ThreeDPoint(xRange.first - 1, y, z),
            ThreeDPoint(x = 1)
          )
        }
      }
    }
  }

  fun buildRight(minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    return with(minsAndMaxes) {
      zRange.flatMap { z ->
        yRange.map { y ->
          PointAndMovementDirection(
            ThreeDPoint(xRange.last + 1, y, z),
            ThreeDPoint(x = -1)
          )
        }
      }
    }
  }


  fun buildBottom(minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    return with(minsAndMaxes) {
      xRange.flatMap { x ->
        zRange.map { z ->
          PointAndMovementDirection(
            ThreeDPoint(x, yRange.first - 1, z),
            ThreeDPoint(y = 1)
          )
        }
      }
    }
  }

  fun buildTop(minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    return with(minsAndMaxes) {
      xRange.flatMap { x ->
        zRange.map { z ->
          PointAndMovementDirection(
            ThreeDPoint(x, yRange.last + 1, z),
            ThreeDPoint(y = -1)
          )
        }
      }
    }
  }

  fun buildPointsToCheck(existingPoints: Set<ThreeDPoint>, minsAndMaxes: MinsAndMaxes): List<ThreeDPoint> {
    return with(minsAndMaxes) {
      xRange.flatMap { x ->
        yRange.flatMap { y ->
          zRange.mapNotNull { z ->
            val possiblePoint = ThreeDPoint(x, y, z)
            if (possiblePoint in existingPoints) {
              null
            } else {
              possiblePoint
            }
          }
        }
      }
    }
  }

  fun getPointsThatHaveInwardFacingSides(point: ThreeDPoint, points: Set<ThreeDPoint>, minsAndMaxes: MinsAndMaxes): List<PointAndMovementDirection> {
    val startingPoints = buildExposureCheckStartingPoints(point).map { it.applyOffset() }
    val queue = mutableListOf<PointAndMovementDirection>()
    queue.addAll(startingPoints)

    val hitPoints = mutableListOf<PointAndMovementDirection>()

    while (queue.isNotEmpty()) {
      val currentPointAndMovementDirection = queue.removeFirst()

      if (isOutOfBounds(currentPointAndMovementDirection.point, minsAndMaxes)) {
        return emptyList()
      }

      val hitExistingPoint = currentPointAndMovementDirection.point in points

      if (!hitExistingPoint) {
        queue.add(currentPointAndMovementDirection.applyOffset())
      } else {
        hitPoints.add(currentPointAndMovementDirection)
      }
    }
    return hitPoints
  }

  fun isOutOfBounds(point: ThreeDPoint, minsAndMaxes: MinsAndMaxes): Boolean {
    val (x, y, z) = point
    return with(minsAndMaxes) {
      x !in xRange || y !in yRange || z !in zRange
    }
  }

  fun buildExposureCheckStartingPoints(point: ThreeDPoint): List<PointAndMovementDirection> {
    val negativeX = ThreeDPoint(x = -1)
    val positiveX = ThreeDPoint(x = 1)
    val negativeY = ThreeDPoint(y = -1)
    val positiveY = ThreeDPoint(y = 1)
    val negativeZ = ThreeDPoint(z = -1)
    val positiveZ = ThreeDPoint(z = 1)
    return listOf(
      PointAndMovementDirection(point, positiveX),
      PointAndMovementDirection(point, positiveY),
      PointAndMovementDirection(point, positiveZ),
      PointAndMovementDirection(point, negativeX),
      PointAndMovementDirection(point, negativeY),
      PointAndMovementDirection(point, negativeZ),
    )
  }

  fun String.buildPoint(): ThreeDPoint {
    val (x, y, z) = split(",").map { it.toInt() }
    return ThreeDPoint(x, y, z)
  }
}

data class PointAndMovementDirection(
  val point: ThreeDPoint,
  val offset: ThreeDPoint
) {
  fun applyOffset(): PointAndMovementDirection {
    val newPoint = point + offset
    return copy(point = newPoint)
  }
}

data class MinsAndMaxes(
  val xRange: IntRange,
  val yRange: IntRange,
  val zRange: IntRange,
)

suspend fun main() {
  BouldersPart2().calculateSurfaceArea()
}