package com.codedchai.day02

import java.io.File

class RockPaperScissorsPart2 {
  val rock = Element(
    name = ElementName.ROCK,
    enemyCode = 'A',
    myCode = 'X',
    score = 1,
    desiredOutcome = Outcome.LOSE
  )

  val paper = Element(
    name = ElementName.PAPER,
    enemyCode = 'B',
    myCode = 'Y',
    score = 2,
    desiredOutcome = Outcome.DRAW
  )

  val scissors = Element(
    name = ElementName.SCISSORS,
    enemyCode = 'C',
    myCode = 'Z',
    score = 3,
    desiredOutcome = Outcome.WIN
  )

  val elements = listOf(rock, paper, scissors)

  val winScore = 6
  val drawScore = 3
  val loseScore = 0

  fun calculateTotalScore() {
    var totalScore = 0

    File("resources/day02/input.txt").forEachLine { line ->
      val enemyCode = line[0]
      val myCode = line[2]

      val enemyElement = elements.first { it.enemyCode == enemyCode }
      val myOutcome = elements.first { it.myCode == myCode }

      val playedScore = getElementIShouldPlay(enemyElement.name, myOutcome.desiredOutcome)
        .let { getElementScore(it) }

      val roundScore = getOutcomeScore(myOutcome.desiredOutcome)

      totalScore += (roundScore + playedScore)
    }

    println(totalScore)
  }

  fun getOutcomeScore(outcome: Outcome): Int {
    return when (outcome) {
      Outcome.WIN -> winScore
      Outcome.LOSE -> loseScore
      Outcome.DRAW -> drawScore
    }
  }

  fun getElementScore(elementName: ElementName): Int {
    return when (elementName) {
      ElementName.ROCK -> 1
      ElementName.PAPER -> 2
      ElementName.SCISSORS -> 3
    }
  }

  fun getElementIShouldPlay(enemyElement: ElementName, desiredOutcome: Outcome): ElementName {
    return when {
      desiredOutcome == Outcome.DRAW -> enemyElement
      desiredOutcome == Outcome.WIN && enemyElement == ElementName.PAPER -> ElementName.SCISSORS
      desiredOutcome == Outcome.WIN && enemyElement == ElementName.ROCK -> ElementName.PAPER
      desiredOutcome == Outcome.WIN && enemyElement == ElementName.SCISSORS -> ElementName.ROCK
      desiredOutcome == Outcome.LOSE && enemyElement == ElementName.SCISSORS -> ElementName.PAPER
      desiredOutcome == Outcome.LOSE && enemyElement == ElementName.ROCK -> ElementName.SCISSORS
      desiredOutcome == Outcome.LOSE && enemyElement == ElementName.PAPER -> ElementName.ROCK
      else -> enemyElement
    }
  }
}

fun main() {
  RockPaperScissorsPart2().calculateTotalScore()
}
