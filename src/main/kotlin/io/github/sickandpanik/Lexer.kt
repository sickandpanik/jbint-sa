package io.github.sickandpanik

import java.io.File

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
                    block.value in Keyword.stringToKeyword.keys -> tokens.add(Token.KeywordToken(filePos, Keyword.stringToKeyword[block.value]!!))
                    else -> {
                        Regex("[a-z()+\\-*/<>=]|[0-9]+").findAll(block.value).forEach {
                            val newFilePos = FilePosition(filePos.line, filePos.column + it.range.first)
                            when {
                                it.value == "(" -> tokens.add(Token.LeftParenthesisToken(newFilePos))
                                it.value == ")" -> tokens.add(Token.RightParenthesisToken(newFilePos))
                                it.value == "=" -> tokens.add(Token.KeywordToken(newFilePos, Keyword.EQUALS))
                                it.value in Operation.stringToOperation.keys -> tokens.add(Token.OperationToken(newFilePos, Operation.stringToOperation[it.value]!!))
                                it.value.all { it.isDigit() } -> {
                                    try {
                                        it.value.toInt()
                                    } catch (e: NumberFormatException) {
                                        throw LexerException("Numeric constant ${it.value} at $newFilePos not of Int type")
                                    }
                                    tokens.add(Token.ConstantToken(newFilePos, it.value.toInt()))
                                }
                                it.value.all { it in ('a'..'z') } -> tokens.add(Token.VariableToken(newFilePos, it.value))
                                else -> throw Error("Lexer internal error")
                            }
                        }
                    }
                }
            }

            lineNumber++
        }

        return tokens
    }
}