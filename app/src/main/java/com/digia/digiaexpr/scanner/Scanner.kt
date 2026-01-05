package com.digia.digiaexpr.scanner

import com.digia.digiaexpr.token.TokenType
import com.digia.digiaexpr.token.Token

class Scanner(private val source: String) {
    
    private var currIdx = 0
    private var line = 1
    private val tokens = mutableListOf<Token>()
    
    private val digitRegex = Regex("[0-9]")
    
    // Private util functions
    private fun advance(by: Int = 1) {
        currIdx += by
    }
    
    private fun curr(): Char? = if (isAtEnd()) null else source[currIdx]
    
    private fun next(): Char? {
        advance()
        return if (isAtEnd()) null else curr()
    }
    
    private fun isAtEnd(): Boolean = currIdx >= source.length
    
    private fun peek(): Char? = 
        if (currIdx + 1 >= source.length) null else source[currIdx + 1]
    
    private fun peekNext(): Char? = 
        if (currIdx + 2 > source.length) null else source[currIdx + 2]
    
    private fun addToken(type: TokenType, value: String) {
        tokens.add(Token(type = type, lexeme = value, line = line))
    }
    
    fun scanTokens(): List<Token> {
        val charMap = mapOf<Char, (Char) -> Unit>(
            '(' to { char ->
                addToken(TokenType.LEFT_PAREN, char.toString())
                advance()
            },
            ')' to { char ->
                addToken(TokenType.RIGHT_PAREN, char.toString())
                advance()
            },
            ',' to { char ->
                addToken(TokenType.COMMA, char.toString())
                advance()
            },
            '.' to { char ->
                addToken(TokenType.DOT, char.toString())
                advance()
            },
            ';' to { char ->
                addToken(TokenType.SEMICOLON, char.toString())
                advance()
            },
            '\n' to { char ->
                line++
                addToken(TokenType.NEW_LINE, char.toString())
                advance()
            }
        )
        
        while (!isAtEnd()) {
            val char = curr()!!
            
            if (charMap.containsKey(char)) {
                charMap[char]!!.invoke(char)
                continue
            }
            
            if (Regex("\\s").matches(char.toString())) {
                scanWhiteSpace()
                continue
            }
            
            // Scan a String token
            if (char == '"' || char == '\'') {
                scanString()
                continue
            }
            
            // Scan a Integer or Float token
            if (digitRegex.matches(char.toString())) {
                scanNumber()
                continue
            }
            
            if (Regex("[a-zA-Z]").matches(char.toString())) {
                scanIdentifier()
                continue
            }
            
            throw UnsupportedOperationException("Unknown token $char")
        }
        
        addToken(TokenType.EOF, "")
        
        return tokens
    }
    
    private fun scanWhiteSpace() = advance()
    
    private fun scanString() {
        val leftQuotePos = currIdx
        var rightQuotePos = currIdx
        var previousChar = curr()
        var char = next()
        
        // Match opening quote & allow Escaped Characters
        while (char != source[leftQuotePos] && previousChar != '\\' && !isAtEnd()) {
            previousChar = char
            char = next()
            rightQuotePos = currIdx
            
            if (isAtEnd()) {
                throw IllegalStateException("Unterminated String")
            }
        }
        
        // Skip the Closing Quote
        advance()
        addToken(TokenType.STRING, source.substring(leftQuotePos + 1, rightQuotePos))
    }
    
    private fun scanNumber() {
        var numTokenType = TokenType.INTEGER
        val numStartPos = currIdx
        var numEndPos = currIdx
        var char = curr()
        
        while (char != null && digitRegex.matches(char.toString())) {
            if (peek() == '.') {
                if (digitRegex.matches(peekNext()?.toString() ?: "")) {
                    numTokenType = TokenType.FLOAT
                    advance(by = 2)
                } else {
                    throw IllegalStateException("Invalid Number format")
                }
            }
            char = next()
            numEndPos = currIdx
        }
        
        addToken(numTokenType, source.substring(numStartPos, numEndPos))
    }
    
    private fun scanIdentifier() {
        var char = curr()
        val nameStartPos = currIdx
        var nameEndPos = currIdx
        
        while (char != null && Regex("[a-zA-Z0-9_]").matches(char.toString())) {
            char = next()
            nameEndPos = currIdx
        }
        
        val string = source.substring(nameStartPos, nameEndPos)
        when (string) {
            "true", "True" -> addToken(TokenType.YES, "true")
            "false", "False" -> addToken(TokenType.NO, "false")
            else -> addToken(TokenType.VARIABLE, string)
        }
    }
}
