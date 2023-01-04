package com.codedchai.day18

data class ThreeDPoint(
  val x: Int = 0,
  val y: Int = 0,
  val z: Int = 0
) {

  fun left() = copy(x = x - 1)
  fun right() = copy(x = x + 1)
  fun down() = copy(y = y - 1)
  fun up() = copy(y = y + 1)
  fun forward() = copy(z = z - 1)
  fun backward() = copy(z = z + 1)
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

  operator fun plus(other: ThreeDPoint): ThreeDPoint {
    return ThreeDPoint(
      x = x + other.x,
      y = y + other.y,
      z = z + other.z
    )
  }
}
