package io.github.sickandpanik

class LexerException(message: String?): Error(message)

class ParserException(token: Token, message: String?): Error("$token: $message")