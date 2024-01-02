package io.github.sickandpanik

object Lexer {
    fun getTokens(programText: String): List<Token> {
        val tokens = mutableListOf<Token>()

        var lineNumber = 1
        programText.split(Regex("\\n|\\r\\n")).forEach { line ->
            Regex("\\S+").findAll(line).forEach { block ->
                val invalidCharacterMatch = Regex("[^a-z0-9()+\\-*/<>=]").find(block.value)

                if (invalidCharacterMatch != null) {
                    throw LexerException("Invalid character at $lineNumber:${block.range.first + invalidCharacterMatch.range.first}")
                }

                val filePos = FilePosition(lineNumber, block.range.first)
                when {
                    block.value in TokenType.stringToKeyword.keys -> tokens.add(Token(TokenType.stringToKeyword[block.value]!!, filePos))
                    else -> {
                        tokens.addAll(
                            Regex("[a-z()+\\-*/<>=]|[0-9]+").findAll(block.value).map {
                                val newFilePos = FilePosition(filePos.line, filePos.column + it.range.first)
                                when {
                                    it.value == "(" -> Token(TokenType.LPAR, newFilePos)
                                    it.value == ")" -> Token(TokenType.RPAR, newFilePos)
                                    it.value == "=" -> Token(TokenType.EQUALS, newFilePos)
                                    it.value in TokenType.stringToOperation.keys -> Token(TokenType.stringToOperation[it.value]!!, newFilePos)
                                    it.value.all { it.isDigit() } -> {
                                        try {
                                            it.value.toInt()
                                        } catch (e: NumberFormatException) {
                                            throw LexerException("Numeric constant ${it.value} at $newFilePos not of Int type")
                                        }
                                        ConstantToken(newFilePos, it.value.toInt())
                                    }
                                    it.value.all { it in ('a'..'z') } -> VariableToken(newFilePos, it.value)
                                    else -> throw Error("Lexer internal error")
                                }
                            }
                        )
                    }
                }
            }

            lineNumber++
        }

        tokens.add(Token(TokenType.EOF, FilePosition(lineNumber, 0)))

        return tokens
    }
}