package io.github.sickandpanik

enum class TokenType {
    VAR, CONST, LPAR, RPAR, OP, KEYWORD
}

enum class Operation {
    ADD, SUB, MUL, DIV, LT, GT;

    companion object {
        val stringToOperation = mapOf(
            "+" to ADD,
            "-" to SUB,
            "*" to MUL,
            "/" to DIV,
            "<" to LT,
            ">" to GT
        )
    }
}

enum class Keyword {
    EQUALS, WHILE, IF, END;

    companion object {
        val stringToKeyword = mapOf(
            "=" to EQUALS,
            "while" to WHILE,
            "if" to IF,
            "end" to END
        )
    }
}

data class FilePosition(val line: Int, val column: Int) {
    override fun toString(): String = "$line:$column"
}

sealed class Token {
    abstract val tokenType: TokenType
    abstract val filePos: FilePosition

    override fun toString(): String = "token: ${tokenType.name}"

    class VariableToken(override val filePos: FilePosition, val varName: String): Token() {
        override val tokenType = TokenType.VAR

        override fun toString(): String = super.toString() + ", varName: $varName"
    }

    class ConstantToken(override val filePos: FilePosition, val value: Int): Token() {
        override val tokenType = TokenType.CONST

        override fun toString(): String = super.toString() + ", value: $value"
    }

    class LeftParenthesisToken(override val filePos: FilePosition): Token() {
        override val tokenType = TokenType.LPAR
    }

    class RightParenthesisToken(override val filePos: FilePosition): Token() {
        override val tokenType = TokenType.RPAR
    }

    class OperationToken(override val filePos: FilePosition, val operation: Operation): Token() {
        override val tokenType = TokenType.OP

        override fun toString(): String = super.toString() + ", operation: ${operation.name}"
    }

    class KeywordToken(override val filePos: FilePosition, val keyword: Keyword): Token() {
        override val tokenType = TokenType.KEYWORD

        override fun toString(): String = super.toString() + ", keyword: ${keyword.name}"
    }
}
