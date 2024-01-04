package io.github.sickandpanik

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class UnusedAssignmentsAnalysis: CliktCommand(name = "sa") {
    private val file by argument().file(mustExist = true, canBeDir = false, mustBeReadable = true)
    private val printAst by option("--ast").flag()
    private val printLiveVars by option("--livevars").flag()

    override fun run() {
        val programText = file.readText()

        val tokens = Lexer.getTokens(programText)

        val parser = Parser()
        val program = parser.getProgram(tokens)

        if (printAst) {
            println("Parsed AST:")
            program.print()
            println()
        }

        val cfg = AstToCfg.astToCfg(program)

        val analyzer = Analyzer()

        if (printLiveVars) {
            val liveVarsLattice = analyzer.liveVarsAnalysis(cfg.first)

            println("Live variables analysis results:")
            liveVarsLattice.forEach {
                println("⟦${it.key.astNode}⟧ = {${it.value.joinToString(", ")}}")
            }
            println()
        }

        val unusedAssignments = analyzer.unusedAssignmentsAnalysis(cfg.first)

        println("Unused assignments:")
        unusedAssignments.forEach {
            println(it.astNode)
        }
    }
}

fun main(args: Array<String>) = UnusedAssignmentsAnalysis().main(args)