package com.codedchai.day20

import java.io.File

class FileMixingPart1 {
  fun decryptFileByMixing() {
    val startingList = File("resources/day20/input.txt").readLines().map { it.toInt() }
    val mixingList = startingList.indices.toMutableList()

    for (index in startingList.indices) {
      val value = startingList[index]
      val mixingIndex = mixingList.indexOf(index)
      mixingList.removeAt(mixingIndex)
      val newIndex = (mixingIndex + value).mod(mixingList.size)
      mixingList.add(newIndex, index)
    }
    printListValues(mixingList, startingList)

    val indexValueOfZero = startingList.indexOf(0)
    val mixingListIndexValueOfZero = mixingList.indexOf(indexValueOfZero)
    val oneIdx = (mixingListIndexValueOfZero + 1000).mod(mixingList.size)
    val twoIdx = (mixingListIndexValueOfZero + 2000).mod(mixingList.size)
    val threeIdx = (mixingListIndexValueOfZero + 3000).mod(mixingList.size)

    val thousandthNumber = startingList[mixingList[oneIdx]]
    val twoThousandthNumber = startingList[mixingList[twoIdx]]
    val threeThousandthNumber = startingList[mixingList[threeIdx]]

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
  FileMixingPart1().decryptFileByMixing()
}