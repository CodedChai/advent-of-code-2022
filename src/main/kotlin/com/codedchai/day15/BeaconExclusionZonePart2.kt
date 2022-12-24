package com.codedchai.day15

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class BeaconExclusionZonePart2 {
  suspend fun findBeaconTuningFrequency(maxValue: Long) {
    val lines = File("resources/day15/input.txt").readLines()
    val beacons = lines.map { buildDiamond(it) }
    var beaconLocation: Point? = null

    withContext(Dispatchers.Default.limitedParallelism(4)) {
      for (xPosition in (0..maxValue).reversed()) {
        launch {
          val beaconRangeForCol = beacons
            .mapNotNull { it.colRange(xPosition, maxValue) }

          val combinedRanges = combineRanges(beaconRangeForCol)

          if (combinedRanges.size > 1) {
            for (yPosition in 0..maxValue) {
              if (beaconRangeForCol.any { it.contains(yPosition) }) {
                continue
              }
              val searchPoint = Point(xPosition, yPosition)
              println(searchPoint)
              beaconLocation = searchPoint
              break
            }
          }
        }
        if (beaconLocation != null) {
          break
        }
      }
    }

    println(beaconLocation)
    println(getTuningFrequency(beaconLocation!!))
  }

  fun combineRanges(ranges: List<LongRange>): List<LongRange> {
    val rangeQueue = ranges.sortedBy { it.first }.toMutableList()

    val mergedRanges = mutableListOf(rangeQueue.removeFirst())

    for (currRange in rangeQueue) {
      val lastMergedRange = mergedRanges.last()
      if (currRange.first <= lastMergedRange.last || lastMergedRange.last + 1 == currRange.first) {
        val combinedRange = combineRange(currRange, lastMergedRange)
        val lastIndex = mergedRanges.size - 1
        mergedRanges[lastIndex] = combinedRange
      } else {
        mergedRanges.add(currRange)
      }
    }

    return mergedRanges
  }

  fun rangesOverlap(range1: LongRange, range2: LongRange): Boolean {
    return range1.first in range2 || range1.last in range2 || range2.first in range1 || range2.last in range2
  }

  fun combineRange(range1: LongRange, range2: LongRange): LongRange {
    val start = minOf(range1.last, range2.last)
    val end = maxOf(range1.last, range2.last)
    return start..end
  }

  fun getTuningFrequency(point: Point): Long {
    return point.x * 4000000 + point.y
  }

  fun buildDiamond(line: String): Diamond {
    val splitOnX = line.split("x=")
    val sensorX = splitOnX[1].split(",")[0].toLong()
    val beaconX = splitOnX[2].split(",")[0].toLong()
    val splitOnY = line.split("y=")
    val sensorY = splitOnY[1].split(":")[0].toLong()
    val beaconY = splitOnY[2].toLong()

    val sensorPoint = Point(sensorX, sensorY)
    val beaconPoint = Point(beaconX, beaconY)
    val manhattenDistance = sensorPoint.getManhattenDistance(beaconPoint)

    return Diamond(sensorPoint, beaconPoint, manhattenDistance)
  }
}

suspend fun main() {
  BeaconExclusionZonePart2().findBeaconTuningFrequency(4000000)
}