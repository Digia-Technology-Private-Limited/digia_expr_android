package com.digia.digiaexpr.visitor

import com.digia.digiaexpr.ast.*

abstract class Visitor<T> {
    open fun visitAst(node: ASTNode): T {
        throw NotImplementedError("${node::class.simpleName} is not implemented")
    }
    
    abstract fun visitProgram(node: ASTProgram): T
    abstract fun visitCallExpression(node: ASTCallExpression): T
    abstract fun visitASTNumberLiteral(node: ASTNumberLiteral): T
    abstract fun visitASTStringExpression(node: ASTStringExpression): T
    abstract fun visitASTStringLiteral(node: ASTStringLiteral): T
    abstract fun visitASTVariable(node: ASTVariable): T
    abstract fun visitASTGetExpr(node: ASTGetExpr): T
    abstract fun visitASTBooleanLiteral(node: ASTBooleanLiteral): T
}
