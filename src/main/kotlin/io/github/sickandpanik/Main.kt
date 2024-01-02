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

        val tokens = Lexer.getTokens(programText)

        tokens.forEach {
            println(it)
        }

//        val program = AstProgram(AstStatementList(listOf(
//            AstAssignment(AstVariable("a"), AstBinaryOp(Operation.ADD, AstConstant(2), AstConstant(2))),
//            AstIfBlock(AstBinaryOp(Operation.GT, AstConstant(-3), AstConstant(1)), AstStatementList(listOf()))
//        )))
//        program.print()

        val parser = Parser()
        val program = parser.getProgram(tokens)

        if (printAst) {
            program.print()
        }
    }
}

fun main(args: Array<String>) = UnusedAssignmentsAnalysis().main(args)