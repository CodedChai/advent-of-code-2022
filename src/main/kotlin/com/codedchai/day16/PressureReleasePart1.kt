package com.codedchai.day16

import java.io.File
import java.util.*

class PressureReleasePart1 {
  fun process() {
    val valves = File("resources/day16/input.txt").readLines().map { buildValve(it) }

    valves.forEach { println(it) }
    relieveTheMostPressure(valves, "AA")
  }

  fun relieveTheMostPressure(valves: List<Valve>, startingValveName: String) {
    val allValvesToOpen = valves.filter { it.flowRate > 0 }.map { it.name }
    val firstPressurePath = PressurePath(startingValveName)
    // Use a stack to reduce the amount of data at once since I don't have much RAM
    val stack = LinkedList<PressurePath>()
    stack.add(firstPressurePath)
    var currentMaxPressureRelieved = 0
    while (stack.isNotEmpty()) {
      val currentPressurePath = stack.pollLast()

      if (currentPressurePath.minute == currentPressurePath.finalMinute) {
        val previousPressure = currentMaxPressureRelieved
        currentMaxPressureRelieved = maxOf(currentPressurePath.totalPressureRelieved, currentMaxPressureRelieved)

        if (previousPressure != currentMaxPressureRelieved) {
          println("Queue size: ${stack.size} & current max: $currentMaxPressureRelieved")
        }
        continue
      }

      // Estimate that if we're 5 minutes left and not halfway to our current max then we probably won't ever reach it
      if (currentPressurePath.minute >= currentPressurePath.finalMinute - 5 && currentPressurePath.totalPressureRelieved < currentMaxPressureRelieved / 2) {
        //  println("Skipping: $currentPressurePath")
        continue
      }
//      if (currentMaxPressureRelieved >= 1500) {
//        println(currentPressurePath)
//      }
      val actions = getAllPossibleActions(currentPressurePath, valves, allValvesToOpen)
      val newPossiblePaths = takeActions(currentPressurePath, valves, actions)

      stack.addAll(newPossiblePaths)
//      stack.removeIf { it.minute >= it.finalMinute - 8 && it.totalPressureRelieved < currentMaxPressureRelieved / 2 }
      stack.sortedWith(compareBy<PressurePath> { it.totalPressureRelieved }.thenBy { it.minute })
    }
    println(currentMaxPressureRelieved)
  }

  fun takeActions(pressurePath: PressurePath, valves: List<Valve>, actions: List<Action>): List<PressurePath> {
    return actions.flatMap { action ->
      when (action) {
        Action.TRAVELING_THROUGH_TUNNEL -> travel(pressurePath, valves)
        Action.WAITING -> listOf(wait(pressurePath))
        Action.OPENING_VALVE -> listOf(openValve(pressurePath, valves))
      }
    }
  }

  fun openValve(pressurePath: PressurePath, valves: List<Valve>): PressurePath {
    val flowRate = valves.first { it.name == pressurePath.currentValveName }.flowRate
    // valve technically opens when the action is complete so open add one minute
    val totalFlowRate = flowRate * (pressurePath.finalMinute - (pressurePath.minute + 1))
    return pressurePath.copy(
      openedValves = pressurePath.openedValves + pressurePath.currentValveName,
      totalPressureRelieved = pressurePath.totalPressureRelieved + totalFlowRate,
      minute = pressurePath.minute + 1
    )
  }

  fun wait(pressurePath: PressurePath): PressurePath {
    return pressurePath.copy(minute = pressurePath.finalMinute)
  }

  fun travel(pressurePath: PressurePath, valves: List<Valve>): List<PressurePath> {
    val possibleTravelLocations = valves.first { it.name == pressurePath.currentValveName }.connectedValves
    val nextPaths = possibleTravelLocations.filter { possibleLocation ->
      (pressurePath.visitedValves[possibleLocation] ?: 0) < 6 // Try to prevent loops
    }.map { newValveName ->
      val newVisitedValves = pressurePath.visitedValves.toMutableMap()
      newVisitedValves[pressurePath.currentValveName] = (newVisitedValves[pressurePath.currentValveName] ?: 0) + 1
      pressurePath.copy(
        currentValveName = newValveName,
        visitedValves = newVisitedValves.toMap(),
        minute = pressurePath.minute + 1
      )
    }
    return nextPaths.ifEmpty {
      listOf(pressurePath.copy(minute = pressurePath.finalMinute))
    }
  }

  fun getAllPossibleActions(pressurePath: PressurePath, valves: List<Valve>, allValvesToOpen: List<String>): List<Action> {
    val alreadyOpenedAllValves = allValvesToOpen.all { it in pressurePath.openedValves }
//    val alreadyTraveledToNeighbors = valves.first { it.name == pressurePath.currentValveName }.connectedValves.all { neighborName ->
//      val numberOfVisits = pressurePath.visitedValves[neighborName] ?: 0
//      numberOfVisits > 2
//    }
    if (alreadyOpenedAllValves /*|| alreadyTraveledToNeighbors*/) {
      return listOf(Action.WAITING)
    }
    val currentValve = valves.first { it.name == pressurePath.currentValveName }
    return if (currentValve.flowRate == 0 || currentValve.name in pressurePath.openedValves) {
      listOf(Action.TRAVELING_THROUGH_TUNNEL)
    } else if (currentValve.flowRate > 10) { // we wouldn't want to skip high value ones
      listOf(Action.OPENING_VALVE)
    } else {
      listOf(Action.OPENING_VALVE, Action.TRAVELING_THROUGH_TUNNEL)
    }
  }

  fun buildValve(line: String): Valve {
    val name = line.removePrefix("Valve ").split(" ").first()
    val flowRate = line.split("=")[1].split(";").first().toInt()
    val connectedValves = line.split("to valve").last().replace(" ", "").replace("s", "").split(",")
    return Valve(name, flowRate, connectedValves)
  }
}

data class PressurePath(
  val currentValveName: String,
  val openedValves: Set<String> = hashSetOf(),
  val visitedValves: Map<String, Int> = mapOf(),
  val minute: Int = 1,
  val totalPressureRelieved: Int = 0,
  val finalMinute: Int = 30
)

enum class Action {
  OPENING_VALVE,
  TRAVELING_THROUGH_TUNNEL,
  WAITING
}

data class Valve(
  val name: String,
  val flowRate: Int,
  val connectedValves: List<String>
)

fun main() {
  PressureReleasePart1().process()
}