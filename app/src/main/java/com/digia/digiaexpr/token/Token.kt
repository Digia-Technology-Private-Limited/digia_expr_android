package com.digia.digiaexpr.token

enum class TokenType {
    // Single-character tokens.
    LEFT_PAREN,
    RIGHT_PAREN,
    COMMA,
    DOT,
    SEMICOLON,
    NEW_LINE,
    
    // Literals.
    VARIABLE,
    STRING,
    INTEGER,
    FLOAT,
    
    // Keywords.
    NO,
    YES,
    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val line: Int
) {
    override fun toString(): String {
        return "{ type: $type, lexeme: $lexeme, line: $line }"
    }
}
