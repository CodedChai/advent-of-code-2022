package com.codedchai.day20

import java.io.File

class FileMixingPart2 {
  fun decryptFileByMixing() {
    val startingList = File("resources/day20/input.txt").readLines().map { it.toInt() }

    println(startingList)
    val mixingList = startingList.indices.toMutableList()

    repeat(10) {
      println(it)
      for (index in startingList.indices) {
        val value = startingList[index]
        val mixingIndex = mixingList.indexOf(index)
        mixingList.removeAt(mixingIndex)
        val newIndex = (mixingIndex + (value * 811589153L)).mod(mixingList.size)
        mixingList.add(newIndex, index)
      }
      printListValues(mixingList, startingList)
    }

    val indexValueOfZero = startingList.indexOf(0)
    val mixingListIndexValueOfZero = mixingList.indexOf(indexValueOfZero)
    val oneIdx = (mixingListIndexValueOfZero + 1000).mod(mixingList.size)
    val twoIdx = (mixingListIndexValueOfZero + 2000).mod(mixingList.size)
    val threeIdx = (mixingListIndexValueOfZero + 3000).mod(mixingList.size)

    val thousandthNumber = startingList[mixingList[oneIdx]] * 811589153L
    val twoThousandthNumber = startingList[mixingList[twoIdx]] * 811589153L
    val threeThousandthNumber = startingList[mixingList[threeIdx]] * 811589153L

    val answer = thousandthNumber + twoThousandthNumber + threeThousandthNumber
    println("Answer: $answer")
  }

  private fun printListValues(mixingList: MutableList<Int>, startingList: List<Int>) {
    print("[")
    mixingList.forEach { index ->
      print("${startingList[index]}, ")
    }
    print("]")
    println()
  }
}

fun main() {
  FileMixingPart2().decryptFileByMixing()
}