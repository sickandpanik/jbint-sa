package io.github.sickandpanik

class Parser {
    private var position: Int = 0
    private var tokens: List<Token> = listOf()

    fun getProgram(tokens: List<Token>): AstProgram {
        position = 0
        this.tokens = tokens

        return program()
    }

    private fun program(): AstProgram {
        return AstProgram(statementList())
    }

    private fun statementList(stopOnEnd: Boolean = false): AstStatementList {
        val statementList = mutableListOf<AstStatement>()

        while (!isAtEnd()) {
            if (stopOnEnd && check(TokenType.END)) {
                break
            }

            statementList.add(statement())
        }

        return AstStatementList(statementList)
    }

    private fun statement(): AstStatement {
        return when {
            check(TokenType.VAR) -> assignment()
            check(TokenType.WHILE) -> whileBlock()
            check(TokenType.IF) -> ifBlock()
            else -> throw ParserException(peek(), "Token does not start a statement.")
        }
    }

    private fun assignment(): AstAssignment {
        val varName = (consume(TokenType.VAR) as VariableToken).varName
        consume(TokenType.EQUALS)
        val expr = expression()

        return AstAssignment(AstVariable(varName), expr)
    }

    private fun whileBlock(): AstWhileBlock {
        consume(TokenType.WHILE)
        val expr = expression()
        val statementList = statementList(true)
        consume(TokenType.END)

        return AstWhileBlock(expr, statementList)
    }

    private fun ifBlock(): AstIfBlock {
        consume(TokenType.IF)
        val expr = expression()
        val statementList = statementList(true)
        consume(TokenType.END)

        return AstIfBlock(expr, statementList)
    }

    private fun expression(): AstExpression {
        return comparison()
    }

    private fun comparison(): AstExpression {
        var lhs = term()

        while (match(TokenType.GT, TokenType.LT)) {
            val op = Operation.fromToken(previous())
            val rhs = term()
            lhs = AstBinaryOp(op, lhs, rhs)
        }

        return lhs
    }

    private fun term(): AstExpression {
        var lhs = factor()

        while (match(TokenType.ADD, TokenType.SUB)) {
            val op = Operation.fromToken(previous())
            val rhs = factor()
            lhs = AstBinaryOp(op, lhs, rhs)
        }

        return lhs
    }

    private fun factor(): AstExpression {
        var lhs = primary()

        while (match(TokenType.MUL, TokenType.DIV)) {
            val op = Operation.fromToken(previous())
            val rhs = primary()
            lhs = AstBinaryOp(op, lhs, rhs)
        }

        return lhs
    }

    private fun primary(): AstExpression {
        if (match(TokenType.VAR)) {
            return AstVariable((previous() as VariableToken).varName)
        }

        if (match(TokenType.CONST)) {
            return AstConstant((previous() as ConstantToken).value)
        }

        if (match(TokenType.LPAR)) {
            val expr = expression()
            consume(TokenType.RPAR)
            return AstNestedExpr(expr)
        }

        throw ParserException(peek(), "Primary expression (variable, decimal constant or expression wrapped in parentheses) expected.")
    }

    private fun check(tokenType: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return peek().type == tokenType
    }

    private fun match(vararg tokenTypes: TokenType): Boolean {
        tokenTypes.forEach { type ->
            if (check(type)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun advance(): Token {
        if (!isAtEnd()) {
            position++
        }
        return previous()
    }

    private fun consume(tokenType: TokenType, message: String = "Unexpected token"): Token {
        if (check(tokenType)) {
            return advance()
        }

        throw ParserException(peek(), message)
    }

    private fun isAtEnd(): Boolean {
        return tokens[position].type == TokenType.EOF
    }

    private fun peek(): Token {
        return tokens[position]
    }

    private fun previous(): Token {
        return tokens[position - 1]
    }
}