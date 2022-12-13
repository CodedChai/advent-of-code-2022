package com.codedchai.day07

import java.io.File

class NoSpaceLeftPart1 {
  fun findTotalDirSizeForDirsUnder100k() {
    val lines = File("resources/day07/input.txt").readLines()

    val directoriesMap = mutableMapOf<String, Directory>()
    var currentDirectory: Directory? = null

    lines.forEachIndexed { index, s ->
      println(s)
      currentDirectory = when {
        s.isCommand() -> executeCommand(s, currentDirectory, directoriesMap)
        s.isFile() && currentDirectory != null -> addFile(currentDirectory!!, s)
        s.isDir() && currentDirectory != null -> addDir(currentDirectory!!, s)
        else -> throw RuntimeException("Unidentified: $s")
      }
      directoriesMap[currentDirectory!!.path] = currentDirectory!!
      // add all child dirs to this too
      currentDirectory!!.childDirectories.forEach {
        directoriesMap[it.path] = it
      }
    }


    val root = directoriesMap["/"]!!
    setDirectorySizes(root)

    directoriesMap.toSortedMap().forEach { println(it) }

    val answer = directoriesMap.filter { (_, dir) ->
      dir.size!! <= 100000
    }
      .map { (_, dir) -> dir }
      .sumOf { it.size!! }
    println("-----------------")
    println(answer)
  }

  // Sorry for the mutability
  fun setDirectorySizes(directory: Directory): Int {
    directory.size = directory.childDirectories.sumOf {
      setDirectorySizes(it)
    } + directory.files.sumOf { it.size }

    return directory.size!!
  }

  fun addFile(currentDirectory: Directory, fileDetails: String): Directory {
    val (fileSize, fileName) = fileDetails.split(" ")
    val newFile = AdventFile(fileName = fileName, size = fileSize.toInt())
    currentDirectory.files.add(newFile)
    return currentDirectory
  }

  fun addDir(currentDirectory: Directory, dirDetails: String): Directory {
    val dirName = dirDetails.split(" ")[1]
    val newDir = Directory(dirName = dirName, path = "${currentDirectory.path}$dirName/")
    currentDirectory.childDirectories.add(newDir)
    return currentDirectory
  }

  fun executeCommand(line: String, currentDirectory: Directory?, directoriesMap: Map<String, Directory>): Directory {
    return if (line.startsWith("$ cd")) {
      executeCd(line.split(" ")[2], currentDirectory, directoriesMap)
    } else {
      executeLs(currentDirectory!!)
    }
  }

  fun executeLs(currentDirectory: Directory): Directory {
    return currentDirectory // nop, nothing to do for this command in our case
  }

  fun executeCd(param: String, currentDirectory: Directory? = null, directoriesMap: Map<String, Directory>): Directory {
    if (currentDirectory == null) {
      return Directory(dirName = param, path = param)
    }

    if (param == "..") {
      val currentPath = currentDirectory.path
      val currentPathParts = currentPath.split("/").filterNot { it.isBlank() }
      val newDir = currentPathParts.subList(0, currentPathParts.size - 1).joinToString("/")
        .setRoot().addLeadingSlash().addTrailingSlash()
      return directoriesMap[newDir]!!
    }

    return currentDirectory.childDirectories.find { it.dirName == param }
      ?: Directory(dirName = param, path = "${currentDirectory.path}$param")
  }

  fun String.setRoot(): String {
    return if (isBlank()) {
      "/"
    } else {
      this
    }
  }

  fun String.addLeadingSlash(): String {
    return if (this.first() != '/') {
      "/" + this
    } else {
      this
    }
  }

  fun String.addTrailingSlash(): String {
    return if (this.last() != '/') {
      this + "/"
    } else {
      this
    }
  }

  fun String.isCommand() = startsWith("$")
  fun String.isFile() = first().isDigit()

  fun String.isDir() = startsWith("dir")
}

data class AdventFile(
  val fileName: String,
  val size: Int
)

data class Directory(
  val dirName: String,
  val path: String,
  val files: MutableList<AdventFile> = mutableListOf(),
  val childDirectories: MutableList<Directory> = mutableListOf(),
  var size: Int? = null
)

fun main() {
  NoSpaceLeftPart1().findTotalDirSizeForDirsUnder100k()
}