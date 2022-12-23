package com.codedchai.day15

import java.io.File

class BeaconExclusionZonePart1 {
  fun generateDiamonds(searchY: Long) {
    val lines = File("resources/day15/input.txt").readLines()

    val beacons = lines.map { buildDiamond(it) }
    beacons.forEach { println(it) }

    var count = 0
    val maxX = beacons.maxOf { it.manhattenDistance + it.centerPoint.x }
    val minX = beacons.minOf { it.centerPoint.x - it.manhattenDistance }

    println(maxX)
    (minX..maxX).forEach { xPosition ->
      val searchPoint = Point(xPosition, searchY)

      if (beacons.any { it.contains(searchPoint) && it.beaconPoint != searchPoint }) {
        count++
      }
    }
    println(count)

    //visualizeBeacons(beacons)
  }

  fun visualizeBeacons(beacons: List<Diamond>) {
    val maxX = beacons.maxOf { it.manhattenDistance + it.centerPoint.x }
    val maxY = beacons.maxOf { it.manhattenDistance + it.centerPoint.y }
    val minX = beacons.minOf { it.centerPoint.x - it.manhattenDistance }
    val minY = beacons.minOf { it.centerPoint.y - it.manhattenDistance }

    val padLength = maxY.toString().length
    (minY..maxY).forEach { y ->
      println()
      if (y % 5 == 0L) {
        print("$y".padStart(padLength, '0'))
      } else {
        print("".padStart(padLength, ' '))
      }
      (minX..maxX).forEach { x ->
        val pointToDraw = Point(x, y)
        if (beacons.any { it.centerPoint == pointToDraw }) {
          print("S")
        } else if (beacons.any { it.beaconPoint == pointToDraw }) {
          print("B")
        } else if (beacons.any { it.contains(pointToDraw) }) {
          print("#")
        } else {
          print(".")
        }
      }
    }
    println()
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

fun main() {
  BeaconExclusionZonePart1().generateDiamonds(2000000)
}