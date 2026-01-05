package com.digia.digiaexpr

import com.digia.digiaexpr.CreateAST.createAST
import com.digia.digiaexpr.constants.EXPRESSION_SYNTAX_REGEX
import com.digia.digiaexpr.context.ExprContext
import com.digia.digiaexpr.evaluator.ASTEvaluator

object Expression {
    fun eval(source: String, context: ExprContext? = null): Any? {
        val trimmed = source.trim()
        
        when {
            isExpression(trimmed) -> {
                val root = createAST(trimmed.substring(2, trimmed.length - 1))
                return ASTEvaluator(context).eval(root)
            }
            
            hasExpression(trimmed) -> {
                val root = createAST(wrapWithQuotes(trimmed))
                return ASTEvaluator(context).eval(root) as? String
            }
            
            else -> {
                val root = createAST(trimmed)
                return ASTEvaluator(context).eval(root)
            }
        }
    }
    
    fun hasExpression(s: String): Boolean {
        return Regex(EXPRESSION_SYNTAX_REGEX).containsMatchIn(s.trim())
    }
    
    fun isExpression(s: String): Boolean {
        val string = s.trim()
        return string.startsWith("\${") &&
                string.endsWith("}") &&
                Regex(EXPRESSION_SYNTAX_REGEX).findAll(s).count() == 1
    }
    
    private fun wrapWithQuotes(string: String): String {
        if (string.startsWith("'") && string.endsWith("'")) {
            return string
        }
        return "\"$string\""
    }
}
