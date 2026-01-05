package com.digia.digiaexpr.ast

import com.digia.digiaexpr.token.Token
import com.digia.digiaexpr.types.Getter
import com.digia.digiaexpr.visitor.Visitor

enum class AstNodeType {
    PROGRAM,
    FUNCTION,
    NUMBER_LITERAL,
    STRING_EXPRESSION,
    STRING_LITERAL,
    VARIABLE,
    SUB_EXPRESSION,
    GET_EXPR,
    BOOLEAN
}

abstract class ASTNode {
    abstract val type: AstNodeType
    
    open fun <T> visit(visitor: Visitor<T>): T = visitor.visitAst(this)
}

class ASTBooleanLiteral(val token: Token) : ASTNode() {
    override val type: AstNodeType = AstNodeType.BOOLEAN
    
    val value: Boolean?
        get() = when (token.type) {
            com.digia.digiaexpr.token.TokenType.YES -> true
            com.digia.digiaexpr.token.TokenType.NO -> false
            else -> null
        }
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitASTBooleanLiteral(this)
}

class ASTProgram(val body: MutableList<ASTNode>) : ASTNode() {
    override val type: AstNodeType = AstNodeType.PROGRAM
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitProgram(this)
}

class ASTCallExpression(
    val fnName: ASTNode,
    val expressions: MutableList<ASTNode>
) : ASTNode() {
    override val type: AstNodeType = AstNodeType.FUNCTION
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitCallExpression(this)
}

class ASTNumberLiteral : ASTNode {
    override val type: AstNodeType = AstNodeType.NUMBER_LITERAL
    
    private val _value: Number?
    private val _getter: Getter<Number>?
    
    constructor(value: Number? = null, getter: Getter<Number>? = null) {
        this._value = value
        this._getter = getter
    }
    
    val value: Number?
        get() = _value ?: _getter?.invoke()
    
    val isFloat: Boolean
        get() = value is Double || value is Float
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitASTNumberLiteral(this)
}

class ASTStringExpression(val parts: List<ASTNode>) : ASTNode() {
    override val type: AstNodeType = AstNodeType.STRING_EXPRESSION
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitASTStringExpression(this)
}

class ASTStringLiteral : ASTNode {
    override val type: AstNodeType = AstNodeType.STRING_LITERAL
    
    private val _value: String?
    private val _getter: Getter<String>?
    
    constructor(value: String? = null, getter: Getter<String>? = null) {
        this._value = value
        this._getter = getter
    }
    
    val value: String?
        get() = _value ?: _getter?.invoke()
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitASTStringLiteral(this)
}

class ASTVariable(val name: Token) : ASTNode() {
    override val type: AstNodeType = AstNodeType.VARIABLE
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitASTVariable(this)
}

class ASTGetExpr(
    val name: Token,
    val expr: ASTNode
) : ASTNode() {
    override val type: AstNodeType = AstNodeType.GET_EXPR
    
    override fun <T> visit(visitor: Visitor<T>): T = visitor.visitASTGetExpr(this)
}
