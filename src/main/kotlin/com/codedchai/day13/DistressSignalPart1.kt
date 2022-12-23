package com.codedchai.day13

import java.io.File

class DistressSignalPart1 {

  fun parse() {
    val lines = File("resources/day13/input.txt").readLines()

    val pairs = lines.chunked(3).map {
      it[0] to it[1]
    }

    val result = pairs.mapIndexed { index, packets ->
      val left = buildPacketLine(packets.first).first
      val right = buildPacketLine(packets.second).first

      println(left)
      println(right)

      isInOrder(left, right) to (index + 1)
    }

    println(result)

    val answer = result.filter { it.first }.sumOf { it.second }
    println(answer)
  }

  fun isInOrder(left: PacketLine, right: PacketLine): Boolean {
    val children = mutableListOf<Pair<PacketLine?, PacketLine?>>()
    children.add(left to right)
    var result: Boolean?
    while (children.isNotEmpty()) {
      val (currentLeft, currentRight) = children.removeFirst()
      if (currentLeft == null) {
        return true
      } else if (currentRight == null) {
        return false
      }
      result = compareNumbers(currentLeft.numbers, currentRight.numbers)

      if (result != null) {
        return result
      }

      val maxChildIndex = maxOf(currentLeft.children.size - 1, currentRight.children.size - 1)
      if (maxChildIndex < 0) {
        continue
      }

      for (currChildIndex in 0..maxChildIndex) {
        val leftChild = currentLeft.getChildPacketAt(currChildIndex)
        val rightChild = currentRight.getChildPacketAt(currChildIndex)
        if (leftChild == null && rightChild == null) {
          continue
        }
        children.add(leftChild to rightChild)
      }
    }
    return true
  }

  private fun PacketLine.getChildPacketAt(currChildIndex: Int) = if (currChildIndex < children.size) {
    children[currChildIndex]
  } else {
    null
  }

  fun compareNumbers(left: List<Int>, right: List<Int>): Boolean? {
    var leftIndex = 0
    var rightIndex = 0
    if (right.isEmpty() && left.isNotEmpty()) {
      return false
    } else if (left.isEmpty() && right.isNotEmpty()) {
      return true
    }
    while (leftIndex < left.size) {
      val leftNumber = left[leftIndex]
      val rightNumber = right[rightIndex]
      compareNumber(leftNumber, rightNumber)?.also { return it }

      if (rightIndex == right.size - 1 && right.size < left.size) {
        return false
      } else if (leftIndex == left.size - 1 && left.size < right.size) {
        return true
      }
      leftIndex++
      rightIndex++
    }

    return null
  }

  private fun compareNumber(leftNumber: Int?, rightNumber: Int?): Boolean? {
    val comparison = leftNumber!!.compareTo(rightNumber!!)
    println("comparison: $comparison - $leftNumber - $rightNumber")
    return when (comparison) {
      -1 -> true
      1 -> false
      else -> null
    }
  }

  fun buildPacketLine(packet: String, currentIndex: Int = 0, depth: Int = 0): Pair<PacketLine, Int> {
    var leftCurrentNumber = mutableListOf<Char>()
    val numbersInPacket = mutableListOf<Int>()
    val children = mutableListOf<PacketLine>()
    var index = currentIndex
    while (index < packet.length) {
      //      println("char: $char")
      when (val char = packet[index]) {
        '[' -> {
          val childStuff = buildPacketLine(packet, index + 1, depth + 1)
          index = childStuff.second
          children.add(childStuff.first)
        }

        ']' -> {
          if (leftCurrentNumber.size > 0) {
            numbersInPacket.add(leftCurrentNumber.joinToString("").toInt())
          }
          return if (numbersInPacket.size == 1) { // handle the whole thing where 1 value needs to be in a list too
            val fakeChild = PacketLine(numbersInPacket, depth + 1, emptyList())
            PacketLine(emptyList(), depth, listOf(fakeChild) + children) to index
          } else {
            PacketLine(numbersInPacket, depth, children) to index
          }
        }

        ',' -> {
          if (leftCurrentNumber.size > 0) {
            numbersInPacket.add(leftCurrentNumber.joinToString("").toInt())
            leftCurrentNumber = mutableListOf()
          }
        }

        else -> leftCurrentNumber.add(char)
      }
      index++
    }
    return PacketLine(numbersInPacket, depth, children) to packet.length
  }
}

data class PacketLine(
  val numbers: List<Int>,
  val depth: Int,
  val children: List<PacketLine>
)

fun main() {
  DistressSignalPart1().parse()
}