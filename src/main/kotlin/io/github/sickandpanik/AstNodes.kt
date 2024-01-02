package io.github.sickandpanik

import kotlin.reflect.KProperty

enum class Operation {
    ADD, SUB, MUL, DIV, GT, LT;

    companion object{
        fun fromToken(token: Token): Operation = Operation.valueOf(token.type.name)
    }
}

sealed class AstNode {
    override fun toString(): String = objectWithPropertiesToString(this, listOf("children"))

    fun print() {
        print(generateTreeString(StringBuilder()))
    }

    private fun children(): List<AstNode> {
        val properties = this::class.members
            .filterIsInstance(KProperty::class.java)
            .map { it.call(this) }

        val singleNodeProps = properties.filterIsInstance<AstNode>()
        val listOfNodesProps = properties.filterIsInstance<List<AstNode>>().flatten()

        return singleNodeProps + listOfNodesProps
    }

    private fun generateTreeString(stringBuilder: StringBuilder, indent: Int = 0): StringBuilder {
        stringBuilder.appendLine("${generatePrefix(indent)}$this")
        children().forEach {
            it.generateTreeString(stringBuilder, indent + 1)
        }
        return stringBuilder
    }

    companion object {
        private fun generatePrefix(indent: Int): String {
            return when (indent) {
                0 -> ""
                1 -> "└"
                else -> "│".repeat(indent - 1) + "└"
            }
        }
    }
}

class AstProgram(val statementList: AstStatementList): AstNode()

class AstStatementList(val statements: List<AstStatement>): AstNode()

sealed class AstStatement: AstNode()

class AstAssignment(val varName: AstVariable, val expr: AstExpression): AstStatement()

class AstWhileBlock(val expr: AstExpression, val statements: AstStatementList): AstStatement()

class AstIfBlock(val expr: AstExpression, val statements: AstStatementList): AstStatement()

sealed class AstExpression: AstNode()

class AstVariable(val varName: String): AstExpression()

class AstConstant(val value: Int): AstExpression()

class AstBinaryOp(val op: Operation, val lhs: AstExpression, val rhs: AstExpression): AstExpression()

class AstNestedExpr(val expr: AstExpression): AstExpression()


