package com.codedchai.day15

data class Diamond(
  val centerPoint: Point,
  val beaconPoint: Point,
  val manhattenDistance: Long
) {
  fun contains(point: Point): Boolean {
    val distance = centerPoint.getManhattenDistance(point)
    return distance <= manhattenDistance
  }
}
