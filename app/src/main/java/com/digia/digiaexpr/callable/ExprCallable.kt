package com.digia.digiaexpr.callable

import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.token.Token

interface ExprCallable {
    val name: String
    fun arity(): Int
    fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any?
}

class ExprCallableImpl(
    private val _arity: Int = 0,
    val fn: (ASTEvaluator, List<Any>) -> Any?
) : ExprCallable {
    
    override val name: String = "ExprCallableImpl"
    
    override fun arity(): Int = _arity
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        return fn(evaluator, arguments)
    }
}

class ExprClass(
    override val name: String,
    val fields: MutableMap<String, Any?>,
    val methods: Map<String, ExprCallable>
) : ExprCallable {
    
    override fun arity(): Int = 0
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        return ExprClassInstance(klass = this)
    }
}

interface ExprInstance {
    fun getField(name: String): Any?
}

class ExprClassInstance(val klass: ExprClass) : ExprInstance {
    
    fun set(name: Token, value: Any) {
        klass.fields[name.lexeme] = value
    }
    
    override fun getField(name: String): Any? {
        if (klass.fields.containsKey(name)) {
            return klass.fields[name]
        }
        
        if (klass.methods.containsKey(name)) {
            return klass.methods[name]
        }
        
        throw IllegalArgumentException("Undefined property $name")
    }
}
