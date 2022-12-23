package com.codedchai.day15

import kotlin.math.abs

data class Point(
  val x: Long,
  val y: Long
) {
  fun getManhattenDistance(other: Point): Long {
    return (abs(y - other.y) + abs(x - other.x))
  }
}