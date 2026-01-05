package com.digia.digiaexpr.evaluator


import com.digia.digiaexpr.ast.*
import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.callable.ExprInstance
import com.digia.digiaexpr.context.BasicExprContext

import com.digia.digiaexpr.context.ExprContext
import com.digia.digiaexpr.std.StdLibFunctions
import com.digia.digiaexpr.std.json.JsonGetOp
import com.digia.digiaexpr.std.string.ConcatOp

class ASTEvaluator(context: ExprContext? = null) {
    
    private val _context: ExprContext
    
    init {
        // Create stdlib context with all standard functions
        val std = BasicExprContext(variables = StdLibFunctions.functions.toMap())
        
        if (context != null) {
            // Add stdlib as enclosing context so user variables take precedence
            context.addContextAtTail(std)
            _context = context
        } else {
            // Use stdlib as the only context
            _context = std
        }
    }
    
    fun eval(node: ASTNode): Any? {
        return when (node) {
            is ASTProgram -> {
                eval(node.body.first())
            }
            
            is ASTNumberLiteral -> {
                node.value
            }
            
            is ASTBooleanLiteral -> {
                node.value
            }
            
            is ASTStringLiteral -> {
                node.value
            }
            
            is ASTStringExpression -> {
                ConcatOp().call(this, node.parts)
            }
            
            is ASTCallExpression -> {
                val callee = eval(node.fnName)
                if (callee !is ExprCallable) {
                    throw IllegalStateException("Invalid Function: ${node.fnName}")
                }
                callee.call(this, node.expressions)
            }
            
            is ASTVariable -> {
                val record = _context.getValue(node.name.lexeme)
                if (!record.first) {
                    throw IllegalStateException("${node.name.lexeme} is not defined")
                }
                
                if (record.second == null) return null
                
                if (record.second is ASTNode) {
                    eval(record.second as ASTNode)
                } else {
                    record.second
                }
            }
            
            is ASTGetExpr -> {
                val obj = eval(node.expr)
                
                if (obj == null) return null
                
                if (obj is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    return JsonGetOp().call(this, listOf(obj, node.name.lexeme))
                }
                
                if (obj !is ExprInstance) {
                    throw IllegalStateException("Only class instances have properties")
                }
                
                obj.getField(node.name.lexeme)
            }
            
            else -> throw NotImplementedError("${node::class.simpleName} is not implemented")
        }
    }
}
