package com.codedchai.day12

data class Node(
  val coordinates: Coordinates,
  val value: Int,
  val parent: Node? = null
)
