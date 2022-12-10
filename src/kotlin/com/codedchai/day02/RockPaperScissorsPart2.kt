package com.codedchai.day02

import java.io.File

class RockPaperScissorsPart2 {
  val rock = Element(
    name = ElementName.ROCK,
    enemyCode = 'A',
    myCode = 'X',
    score = 1,
  )

  val paper = Element(
    name = ElementName.PAPER,
    enemyCode = 'B',
    myCode = 'Y',
    score = 2
  )

  val scissors = Element(
    name = ElementName.SCISSORS,
    enemyCode = 'C',
    myCode = 'Z',
    score = 3
  )

  val elements = listOf(rock, paper, scissors)

  val winScore = 6
  val drawScore = 3
  val loseScore = 0

  fun calculateTotalScore() {
    var totalScore = 0

    File("resources/day2/input.txt").forEachLine { line ->
      val enemyCode = line[0]
      val myCode = line[2]

      val enemyElement = elements.first { it.enemyCode == enemyCode }
      val myElement = elements.first { it.myCode == myCode }

      val roundScore = getElementScore(enemyElement.name, myElement.name)

      val playedScore = myElement.score

      totalScore += (roundScore + playedScore)
    }

    println(totalScore)
  }

  fun getElementScore(enemyElement: ElementName, myElement: ElementName): Int {
    return when {
      myElement == enemyElement -> drawScore
      myElement == ElementName.ROCK && enemyElement == ElementName.SCISSORS -> winScore
      myElement == ElementName.PAPER && enemyElement == ElementName.ROCK -> winScore
      myElement == ElementName.SCISSORS && enemyElement == ElementName.PAPER -> winScore
      else -> loseScore
    }
  }
}

fun main() {
  RockPaperScissors().calculateTotalScore()
}
