package com.codedchai.day15

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class BeaconExclusionZonePart2 {
  suspend fun findBeaconTuningFrequency(maxValue: Long) {
    val lines = File("resources/day15/input.txt").readLines()

    val beacons = lines.map { buildDiamond(it) }

    val searchPoints = (0..maxValue).flatMap { xPosition ->
      (0..maxValue).map { yPosition ->
        Point(xPosition, yPosition)
      }
    }

    var beaconLocation: Point? = null
    coroutineScope {
      for (searchPoint in searchPoints) {
        launch {
          if (beacons.none { it.contains(searchPoint) }) {
            beaconLocation = searchPoint
          }
        }

        if (beaconLocation != null) {
          break
        }
      }
    }

    println(beaconLocation)

    println(getTuningFrequency(beaconLocation!!))

    //visualizeBeacons(beacons)
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