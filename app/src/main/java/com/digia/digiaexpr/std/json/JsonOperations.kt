package com.digia.digiaexpr.std.json

import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.util.toValue
import com.jayway.jsonpath.JsonPath

object JsonOperations {
    val functions: Map<String, ExprCallable> = mapOf(
        "jsonGet" to JsonGetOp(),
        "get" to JsonGetOp()
    )
}

class JsonGetOp : ExprCallable {
    override val name: String = "jsonGet"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val json: Any? = toValue(evaluator, arguments[0])
        val path: String? = toValue(evaluator, arguments[1])
        
        if (json == null || path == null) return null
        
        return try {
            val jsonPath = JsonPath.compile("\$.$path")
            val result = jsonPath.read<Any>(json)
            result
        } catch (e: Exception) {
            null
        }
    }
}
