package com.codedchai.day02

data class Element(
  val name: ElementName,
  val enemyCode: Char,
  val myCode: Char,
  val score: Int,
  val desiredOutcome: Outcome = Outcome.DRAW
)