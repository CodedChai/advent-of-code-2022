package com.codedchai.day15

import kotlin.math.abs

data class Diamond(
  val centerPoint: Point,
  val beaconPoint: Point,
  val manhattenDistance: Long
) {
  fun contains(point: Point): Boolean {
    val distance = centerPoint.getManhattenDistance(point)
    return distance <= manhattenDistance
  }

  fun colRange(xPos: Long, maxValue: Long): LongRange? {
    val possibleYDistance = manhattenDistance - abs(centerPoint.x - xPos)
    if (possibleYDistance < 0) {
      return null
    }
    val minRange = maxOf(minOf(maxValue, (centerPoint.y - possibleYDistance)), 0)
    val maxRange = maxOf(minOf(centerPoint.y + possibleYDistance, maxValue), 0)
    return minRange..maxRange
  }
}
