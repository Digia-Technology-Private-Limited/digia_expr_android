package com.digia.digiaexpr.std.string

import com.digia.digiaexpr.ast.ASTNode
import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.util.toValue

object StringOperations {
    val functions: Map<String, ExprCallable> = mapOf(
        "concat" to ConcatOp(),
        "concatenate" to ConcatOp(),
        "substring" to SubStringOp()
    )
}

class SubStringOp : ExprCallable {
    override val name: String = "substring"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Cannot resolve for less than 2 arguments")
        }
        
        val string: String? = toValue(evaluator, arguments[0])
        val start: Int? = toValue(evaluator, arguments[1])
        val end: Int? = toValue(evaluator, arguments.getOrNull(2))
        
        if (start == null) return string
        
        return if (end != null) {
            string?.substring(start, end)
        } else {
            string?.substring(start)
        }
    }
}

class ConcatOp : ExprCallable {
    override val name: String = "concat"
    
    override fun arity(): Int = 255
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        val sb = StringBuilder()
        for (arg in arguments) {
            val value = if (arg is ASTNode) {
                evaluator.eval(arg)?.toString()
            } else {
                arg.toString()
            }
            sb.append(value)
        }
        return sb.toString()
    }
}
