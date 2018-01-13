package com.github.kornilova_l.flamegraph.plugin.server.trees.converters

import com.github.kornilova_l.flamegraph.plugin.converters.ProfilerToFlamegraphConverter
import com.github.kornilova_l.flamegraph.plugin.server.trees.FileToCallTracesConverter
import com.github.kornilova_l.flamegraph.proto.TreeProtos

import java.io.*

class YourkitCsvToCallTracesConverter : FileToCallTracesConverter() {
    override fun getId(): String {
        return "yourkit"
    }

    override fun isSupported(file: File): Boolean {
        if (ProfilerToFlamegraphConverter.getFileExtension(file.name) != "csv") {
            return false
        }
        BufferedReader(FileReader(file)).use { reader ->

            reader.readLine() // skip header
            reader.readLine() // skip header

            var line: String? = reader.readLine()

            while (line != null) {
                val parts = line.split("\",\"")
                if (parts.size != 3) {
                    return false
                }
                val firstString = parts[0].removePrefix("\"")
                if (!isMethod(firstString) && !firstString.contains(':')) {
                    return false
                }
                try {
                    Integer.parseInt(parts[1]) // time
                    Integer.parseInt(parts[2].removeSuffix("\"")) // level
                } catch (e: NumberFormatException) {
                    return false
                }

                line = reader.readLine()
            }
        }
        return true
    }

    private fun isMethod(s: String): Boolean {
        val openBracketPos = s.indexOf('(')
        val closeBracketPos = s.indexOf(')')
        return openBracketPos != -1 &&
                closeBracketPos != -1 &&
                openBracketPos < closeBracketPos
    }

    override fun convert(file: File): TreeProtos.Tree {
        return TreeProtos.Tree.newBuilder().build()
    }
}