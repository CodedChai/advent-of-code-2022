package com.codedchai.day19

import kotlinx.coroutines.coroutineScope
import java.io.File

class OreCollectingPart1 {
  suspend fun getQualityLevel() = coroutineScope {
    val robotBlueprints = File("resources/day19/input.txt").readLines().map { parseBlueprint(it) }

    // robotBlueprints.forEach { println(it) }

    val quality = robotBlueprints.map {
      // async {
      val geodeCount = getMaxGeodeBlueprintCanMine(it, 24)

      println("${it.id} mines: $geodeCount geodes for a quality of ${geodeCount * it.id}")
      geodeCount * it.id
      //     }
    }.sum()

    println(quality)
  }

  fun getMaxGeodeBlueprintCanMine(blueprint: Blueprint, finalMinute: Int): Int {
    val initialState = State(
      blueprint = blueprint,
      maxOreCost = blueprint.getMaxCost(ResourceType.ORE),
      maxClayCost = blueprint.getMaxCost(ResourceType.CLAY),
      maxObsidianCost = blueprint.getMaxCost(ResourceType.OBSIDIAN),
    )

    val stack = mutableListOf(initialState)

    val previousStates = hashSetOf(initialState)
    var totalbranchesLookedAt = 0

    val maxesAtMinutes = mutableMapOf<Int, Int>()

    while (stack.isNotEmpty()) {
      val currentState = stack.removeLast()
      totalbranchesLookedAt++

      val minedGeodes = currentState.currentResources[ResourceType.GEODE] ?: 0
      val currentMax = maxesAtMinutes[currentState.minute] ?: 0
      maxesAtMinutes[currentState.minute] = maxOf(currentMax, minedGeodes)

//      if (totalLookedAt % 100 == 0) {
//        println(totalLookedAt)
//        println(currentState)
//      }

      if (currentState.minute >= finalMinute) {
//        val minedGeodes = currentState.currentResources[ResourceType.GEODE] ?: 0
//        if (minedGeodes > maxGeodesMined) {
//          maxGeodesMined = maxOf(maxGeodesMined, minedGeodes)
//          println(currentState)
//        }
        continue
      }

      // order of operations 
      // 1. spend ore
      // 2. mine ore
      // 3. create robot
      // 4. Add state back to queue
      var buildableRobots =
        currentState.getBuildableRobots()
          .filterNot { bot ->
            isBuildingOreBotUnnecessary(bot, currentState, finalMinute, currentState.currentResources) ||
              isBuildingClayBotUnnecessary(bot, currentState, finalMinute, currentState.currentResources) ||
              isBuildingObsidianBotUnnecessary(bot, currentState, finalMinute, currentState.currentResources)
          }
          .sortedByDescending { it.minesResource.ordinal } // Make the geodes first


      val minedResources = currentState.getResourcesMinedInAMinute()

      // new states where you either build each robot OR skip building the bots
      // Possible future heuristics
      // Only build bots if one of the resources go exactly to 0? 
      // Don't add a mine only option if you have more resources than the max bot costs?


      // If we can build a geode bot and two other types of bots then lets get rid of the phase where we dont build a bot
      val mineOnlyTurn =
        if (buildableRobots.size >= 3 || buildableRobots.size >= 2 && buildableRobots.any { it.minesResource == ResourceType.GEODE }) {
          null
        } else {
          (minedResources to null)
        }

      val possibleGeodeBot = buildableRobots.find { it.minesResource == ResourceType.GEODE }
      val possibleObsidianBot = buildableRobots.find { it.minesResource == ResourceType.OBSIDIAN }

      if (possibleGeodeBot == null && possibleObsidianBot != null) {
        buildableRobots = listOf(possibleObsidianBot)
      }

      val buildCostToRobots = (buildableRobots.map { buildableRobot ->
        addResourceMaps(currentState.resourceCostOfRobot(buildableRobot), minedResources) to buildableRobot
      } + listOfNotNull(mineOnlyTurn)).reversed() // reverse to ensure we keep the geodes first because stack


      val newStates = buildCostToRobots.map { currentState.step(it.first, it.second?.minesResource) }
        .filter { previousStates.add(it) }

      // Filter out anything where we know for a fact that we can't reach the same max
      val statesThatAreOkay = newStates.filterNot {
        (currentMax) > (it.currentResources[ResourceType.GEODE] ?: 0)
      }

      stack.addAll(statesThatAreOkay)
    }


    println("Total branches looked at: $totalbranchesLookedAt")
    return maxesAtMinutes[finalMinute]!!
  }

  private fun isBuildingObsidianBotUnnecessary(
    bot: MiningBot,
    currentState: State,
    finalMinute: Int,
    resources: Map<ResourceType, Int>
  ): Boolean {
    val deltaMinutes = finalMinute - currentState.minute

    return bot.minesResource == ResourceType.OBSIDIAN && deltaMinutes *
      currentState.maxObsidianCost <= resources[ResourceType.OBSIDIAN]!! +
      (deltaMinutes * currentState.activeMiningBotTypes.filter { it == ResourceType.OBSIDIAN }.size)
  }

  private fun isBuildingClayBotUnnecessary(
    bot: MiningBot,
    currentState: State,
    finalMinute: Int,
    resources: Map<ResourceType, Int>
  ): Boolean {
    val deltaMinutes = finalMinute - currentState.minute

    return bot.minesResource == ResourceType.CLAY && deltaMinutes *
      currentState.maxClayCost <= resources[ResourceType.CLAY]!! +
      (deltaMinutes * currentState.activeMiningBotTypes.filter { it == ResourceType.CLAY }.size)
  }

  private fun isBuildingOreBotUnnecessary(
    bot: MiningBot,
    currentState: State,
    finalMinute: Int,
    resources: Map<ResourceType, Int>
  ): Boolean {
    val deltaMinutes = finalMinute - currentState.minute

    return bot.minesResource == ResourceType.ORE && deltaMinutes *
      currentState.maxOreCost <= resources[ResourceType.ORE]!! +
      (deltaMinutes * currentState.activeMiningBotTypes.filter { it == ResourceType.ORE }.size)
  }

  fun parseBlueprint(blueprint: String): Blueprint {
    val id = blueprint.split(" ")[1].split(":").first().toInt()
    val oreRobotOreCost = blueprint.split("costs ")[1].split(" ").first().toInt()
    val clayRobotOreCost = blueprint.split("costs ")[2].split(" ").first().toInt()
    val obsidianRobotOreCost = blueprint.split("costs ")[3].split(" ").first().toInt()
    val obsidianRobotClayCost = blueprint.split("costs ")[3].split(" ")[3].toInt()
    val geodeRobotOreCost = blueprint.split("costs ")[4].split(" ").first().toInt()
    val geodeRobotObsidianCost = blueprint.split("costs ")[4].split(" ")[3].toInt()

    val oreRobot = MiningBot(ResourceType.ORE, listOf(ResourceAndCost(ResourceType.ORE, oreRobotOreCost)))
    val clayRobot = MiningBot(ResourceType.CLAY, listOf(ResourceAndCost(ResourceType.ORE, clayRobotOreCost)))
    val obsidianRobot = MiningBot(
      ResourceType.OBSIDIAN,
      listOf(
        ResourceAndCost(ResourceType.ORE, obsidianRobotOreCost),
        ResourceAndCost(ResourceType.CLAY, obsidianRobotClayCost)
      )
    )
    val geodeRobot = MiningBot(
      ResourceType.GEODE,
      listOf(
        ResourceAndCost(ResourceType.ORE, geodeRobotOreCost),
        ResourceAndCost(ResourceType.OBSIDIAN, geodeRobotObsidianCost)
      )
    )

    return Blueprint(id, listOf(oreRobot, clayRobot, obsidianRobot, geodeRobot))
  }
}


fun addResourceMaps(map1: Map<ResourceType, Int>, otherMap: Map<ResourceType, Int>): Map<ResourceType, Int> {
  val allKeys = map1.keys + otherMap.keys

  return allKeys.associateWith { resourceType ->
    (map1[resourceType] ?: 0) + (otherMap[resourceType] ?: 0)
  }
}

data class State(
  val blueprint: Blueprint,
  val activeMiningBotTypes: List<ResourceType> = listOf(ResourceType.ORE),
  val currentResources: Map<ResourceType, Int> = ResourceType.values().associateWith { 0 },
  val minute: Int = 0,
  val maxOreCost: Int,
  val maxClayCost: Int,
  val maxObsidianCost: Int
) {

  fun step(resourceChanges: Map<ResourceType, Int>, newActivingMiningBotType: ResourceType? = null): State {

    val activeBotsList = activeMiningBotTypes + listOfNotNull(newActivingMiningBotType)
    val newResources = addResourceMaps(currentResources, resourceChanges)
    return copy(
      activeMiningBotTypes = activeBotsList,
      currentResources = newResources,
      minute = minute + 1
    )
  }

  fun getResourcesMinedInAMinute(): Map<ResourceType, Int> {
    return activeMiningBotTypes.groupBy { it }.map { (resourceType, list) ->
      resourceType to list.size
    }.toMap()
  }

  fun getBuildableRobots(): List<MiningBot> {
    return blueprint.bots.filter { potentialBotToBuild ->
      potentialBotToBuild.buildRequirements.all { buildRequirements ->
        (currentResources[buildRequirements.resource] ?: 0) >= buildRequirements.cost
      }
    }
  }

  fun resourceCostOfRobot(miningBot: MiningBot): Map<ResourceType, Int> {
    return miningBot.buildRequirements.associate { it.resource to it.cost * -1 }
  }
}

data class Blueprint(
  val id: Int,
  val bots: List<MiningBot>
) {
  fun getMaxCost(resource: ResourceType): Int {
    return bots.maxOf { it.buildRequirements.find { it.resource == resource }?.cost ?: 0 }
  }
}

data class MiningBot(
  val minesResource: ResourceType,
  val buildRequirements: List<ResourceAndCost>
)

data class ResourceAndCost(
  val resource: ResourceType,
  val cost: Int
)

enum class ResourceType {
  ORE,
  CLAY,
  OBSIDIAN,
  GEODE
}

suspend fun main() {
  OreCollectingPart1().getQualityLevel()
}