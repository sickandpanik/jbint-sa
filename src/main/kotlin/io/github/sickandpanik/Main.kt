package io.github.sickandpanik

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class UnusedAssignmentsAnalysis: CliktCommand() {
    private val file by argument().file(mustExist = true, canBeDir = false, mustBeReadable = true)
    private val printAst by option("--ast").flag()
    private val printCfg by option("--cfg").flag()

    override fun run() {
        val programText = file.readText()

        Lexer.getTokens(programText).forEach {
            println(it)
        }
    }
}

fun main(args: Array<String>) = UnusedAssignmentsAnalysis().main(args)