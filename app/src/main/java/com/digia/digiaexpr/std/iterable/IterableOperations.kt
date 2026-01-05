package com.digia.digiaexpr.std.iterable

import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.util.toValue

object IterableOperations {
    val functions: Map<String, ExprCallable> = mapOf(
        "contains" to ContainsOp(),
        "elementAt" to ElementAtOp(),
        "firstElement" to FirstElementOp(),
        "lastElement" to LastElementOp(),
        "skip" to SkipOp(),
        "take" to TakeOp(),
        "reversed" to ReversedOp()
    )
}

class ContainsOp : ExprCallable {
    override val name: String = "contains"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        val arg2: Any? = toValue(evaluator, arguments[1])
        
        return arg1?.contains(arg2) ?: false
    }
}

class ElementAtOp : ExprCallable {
    override val name: String = "elementAt"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        val arg2: Int? = toValue(evaluator, arguments[1])
        
        if (arg2 == null) return null
        
        return arg1?.getOrNull(arg2)
    }
}

class FirstElementOp : ExprCallable {
    override val name: String = "firstElement"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        
        return arg1?.firstOrNull()
    }
}

class LastElementOp : ExprCallable {
    override val name: String = "lastElement"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        
        return arg1?.lastOrNull()
    }
}

class SkipOp : ExprCallable {
    override val name: String = "skip"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        val arg2: Int? = toValue(evaluator, arguments[1])
        
        return arg1?.drop(arg2 ?: 0)
    }
}

class TakeOp : ExprCallable {
    override val name: String = "take"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        val arg2: Int? = toValue(evaluator, arguments[1])
        
        return arg1?.take(arg2 ?: 0)
    }
}

class ReversedOp : ExprCallable {
    override val name: String = "reversed"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: List<*>? = toValue(evaluator, arguments[0])
        
        return arg1?.reversed()
    }
}
