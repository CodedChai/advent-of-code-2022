package com.codedchai.day05

import java.io.File

class SupplyStacksPart2 {
  fun findTopLetters() {
    val lines = File("resources/day05/input.txt").readLines()
    val allStackStates = mutableListOf(buildStacks(lines))
    for (line in lines) {
      if (!hasValidSteps(line)) {
        continue
      }

      val instruction = getInstruction(line)
      println(instruction)
      val newStack = executeInstruction(instruction, allStackStates.last())
      allStackStates.add(newStack)
    }
    println("------------------------")
    allStackStates.forEach {
      println()
      println(it.toSortedMap())
    }
    println("------------------------")
    val finalStack = allStackStates.last().toSortedMap()
    println(finalStack)
    val topLettersInOrder = finalStack.map { (key, value) ->
      value.first()
    }.joinToString("")
    println("Answer: $topLettersInOrder")
  }

  fun executeInstruction(instruction: Instruction, stacks: Map<Int, List<String>>): Map<Int, List<String>> {
    val moveFromList = stacks[instruction.moveFromKey]!!
    val lettersToMove = moveFromList.take(instruction.numberToMove)
    val newMoveFrom = moveFromList.subList(instruction.numberToMove, moveFromList.size)
    val newMoveTo = lettersToMove + stacks[instruction.moveToKey]!!
    val newStacks = stacks.toMutableMap()
    newStacks[instruction.moveToKey] = newMoveTo
    newStacks[instruction.moveFromKey] = newMoveFrom
    return newStacks
  }

  fun getInstruction(line: String): Instruction {
    val splitLine = line.split(' ')
    return Instruction(
      numberToMove = splitLine[1].toInt(),
      moveFromKey = splitLine[3].toInt(),
      moveToKey = splitLine[5].toInt()
    )
  }

  fun hasValidSteps(line: String): Boolean {
    return line.startsWith("move")
  }

  fun buildStacks(lines: List<String>): Map<Int, List<String>> {
    val map = mutableMapOf<Int, List<String>>()
    for (line in lines) {
      if (line.none { it.isLetter() }) {
        break
      }
      line.chunked(4).forEachIndexed { index, chunkedLine ->
        val letter = chunkedLine.find { it.isLetter() }?.toString()

        letter?.also {
          val key = index + 1
          map[key] = (map[key] ?: emptyList()) + letter
        }
      }
    }
    println(map)
    return map.toMap()
  }
}

fun main() {
  SupplyStacksPart2().findTopLetters()
}