package io.github.sickandpanik

enum class TokenType {
    VAR, CONST,
    LPAR, RPAR,
    ADD, SUB, MUL, DIV, LT, GT,
    IF, WHILE, END, EQUALS,
    EOF;

    companion object {
        val stringToKeyword = mapOf(
            "=" to EQUALS,
            "while" to WHILE,
            "if" to IF,
            "end" to END
        )

        val stringToOperation = mapOf(
            "+" to ADD,
            "-" to SUB,
            "*" to MUL,
            "/" to DIV,
            ">" to GT,
            "<" to LT
        )
    }
}

data class FilePosition(val line: Int, val column: Int) {
    override fun toString(): String = "$line:$column"
}

open class Token(val type: TokenType, val filePos: FilePosition) {
    override fun toString(): String = objectWithPropertiesToString(this)
}

class VariableToken(filePos: FilePosition, val varName: String): Token(TokenType.VAR, filePos)

class ConstantToken(filePos: FilePosition, val value: Int): Token(TokenType.CONST, filePos)
