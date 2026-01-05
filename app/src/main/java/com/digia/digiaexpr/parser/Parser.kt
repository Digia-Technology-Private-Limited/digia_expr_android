package com.digia.digiaexpr.parser

import com.digia.digiaexpr.CreateAST.createAST
import com.digia.digiaexpr.ast.*
import com.digia.digiaexpr.constants.EXPRESSION_SYNTAX_REGEX
import com.digia.digiaexpr.token.Token
import com.digia.digiaexpr.token.TokenType

class Parser(private val tokens: List<Token>) {
    
    lateinit var ast: ASTProgram
    
    private var current = 0
    
    private fun peek(): Token? = 
        if (current + 1 < tokens.size) tokens[current + 1] else null
    
    private fun isAtEnd(): Boolean = current > tokens.size
    
    private fun advance(by: Int = 1) {
        current += by
    }
    
    private fun next(): Token = tokens[++current]
    
    private fun consume(tokenType: TokenType, errorMessage: String) {
        if (tokens[current].type == tokenType) {
            advance()
            return
        }
        throw IllegalStateException(errorMessage)
    }
    
    private fun createStringExpression(input: String): List<ASTNode> {
        val regex = Regex(EXPRESSION_SYNTAX_REGEX)
        val parts = mutableListOf<ASTNode>()
        var lastIndex = 0
        
        for (match in regex.findAll(input)) {
            val expression = input.substring(match.range.first, match.range.last + 1)
            parts.add(ASTStringLiteral(value = input.substring(lastIndex, match.range.first)))
            parts.add(createAST(expression.substring(2, expression.length - 1)))
            lastIndex = match.range.last + 1
        }
        
        if (lastIndex < input.length) {
            parts.add(ASTStringLiteral(value = input.substring(lastIndex)))
        }
        
        return parts
    }
    
    fun parse(): ASTNode {
        ast = ASTProgram(body = mutableListOf())
        
        var token = tokens[current]
        
        fun walk(): ASTNode {
            return when (token.type) {
                TokenType.INTEGER -> {
                    advance()
                    ASTNumberLiteral(value = token.lexeme.toIntOrNull())
                }
                
                TokenType.FLOAT -> {
                    advance()
                    ASTNumberLiteral(value = token.lexeme.toDoubleOrNull())
                }
                
                TokenType.YES, TokenType.NO -> {
                    advance()
                    ASTBooleanLiteral(token = token)
                }
                
                TokenType.STRING -> {
                    if (Regex(EXPRESSION_SYNTAX_REGEX).containsMatchIn(token.lexeme)) {
                        val parts = createStringExpression(token.lexeme)
                        advance()
                        ASTStringExpression(parts = parts)
                    } else {
                        advance()
                        ASTStringLiteral(value = token.lexeme)
                    }
                }
                
                TokenType.SEMICOLON -> {
                    advance()
                    ASTStringLiteral(value = token.lexeme)
                }
                
                TokenType.EOF -> {
                    advance()
                    ASTStringLiteral(value = token.lexeme)
                }
                
                TokenType.VARIABLE -> {
                    // if Neither x.y nor x(), then 'x' is a Variable.
                    if (peek()?.type != TokenType.LEFT_PAREN && 
                        peek()?.type != TokenType.DOT) {
                        advance()
                        return ASTVariable(name = token)
                    }
                    
                    // While loop for the chain.
                    // a.b.c, Or,
                    // a.b.c(), Or,
                    // a.b().c
                    var expr: ASTNode = ASTVariable(name = token)
                    while (peek()?.type == TokenType.LEFT_PAREN || 
                           peek()?.type == TokenType.DOT) {
                        when (peek()?.type) {
                            TokenType.LEFT_PAREN -> {
                                advance()
                                val node = ASTCallExpression(fnName = expr, expressions = mutableListOf())
                                token = next()
                                while (token.type != TokenType.RIGHT_PAREN) {
                                    node.expressions.add(walk())
                                    token = tokens[current]
                                    if (token.type != TokenType.RIGHT_PAREN) {
                                        consume(TokenType.COMMA, "Expected , after a function argument")
                                    }
                                    token = tokens[current]
                                }
                                expr = node
                            }
                            
                            TokenType.DOT -> {
                                advance()
                                expr = ASTGetExpr(name = next(), expr = expr)
                            }
                            
                            else -> break
                        }
                    }
                    advance()
                    expr
                }
                
                else -> throw IllegalStateException("Unexpected token: ${token.type}")
            }
        }
        
        ast.body.add(walk())
        
        return ast
    }
}
