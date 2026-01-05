package com.digia.digiaexpr.std.logical

import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.util.toValue

object LogicalOperations {
    val functions: Map<String, ExprCallable> = mapOf(
        "isEqual" to IsEqualOp(),
        "isNotEqual" to IsNotEqualOp(),
        "isNull" to IsNullOp(),
        "isNotNull" to IsNotNullOp(),
        "condition" to IfOp(),
        "if" to IfOp(),
        "eq" to IsEqualOp(),
        "neq" to IsNotEqualOp(),
        "gt" to GreatThanOp(),
        "gte" to GreatThanOrEqualOp(),
        "lt" to LessThanOp(),
        "lte" to LessThanOrEqualOp(),
        "not" to NotOp(),
        "or" to OrOp(),
        "and" to AndOp()
    )
}

class IfOp : ExprCallable {
    override val name: String = "if"
    
    override fun arity(): Int = 255
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < 2) {
            throw IllegalArgumentException("Cannot resolve for less than 2 arguments")
        }
        
        val defaultCase = if (arguments.size % 2 == 1) arguments.last() else null
        val lengthToIterateOver = arguments.size - (arguments.size % 2)
        
        for (i in 0 until lengthToIterateOver step 2) {
            val condition: Boolean? = toValue(evaluator, arguments[i])
            if (condition == true) {
                return toValue<Any>(evaluator, arguments[i + 1])
            }
        }
        
        return if (defaultCase == null) null else toValue<Any>(evaluator, defaultCase)
    }
}

class IsEqualOp : ExprCallable {
    override val name: String = "isEqual"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Any? = toValue(evaluator, arguments[0])
        val arg2: Any? = toValue(evaluator, arguments[1])
        
        return arg1 == arg2
    }
}

class IsNotEqualOp : ExprCallable {
    override val name: String = "isNotEqual"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Any? = toValue(evaluator, arguments[0])
        val arg2: Any? = toValue(evaluator, arguments[1])
        
        return arg1 != arg2
    }
}

class IsNullOp : ExprCallable {
    override val name: String = "isNull"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size > arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        return arguments.firstOrNull() == null ||
                toValue<Any>(evaluator, arguments.firstOrNull()!!) == null
    }
}

class IsNotNullOp : ExprCallable {
    override val name: String = "isNotNull"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size > arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        return arguments.firstOrNull() != null &&
                toValue<Any>(evaluator, arguments.firstOrNull()!!) != null
    }
}

class GreatThanOp : ExprCallable {
    override val name: String = "gt"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Number? = toValue(evaluator, arguments[0])
        val arg2: Number? = toValue(evaluator, arguments[1])
        
        if (arg1 == null || arg2 == null) return false
        
        return arg1.toDouble() > arg2.toDouble()
    }
}

class GreatThanOrEqualOp : ExprCallable {
    override val name: String = "gte"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Number? = toValue(evaluator, arguments[0])
        val arg2: Number? = toValue(evaluator, arguments[1])
        
        if (arg1 == null || arg2 == null) return false
        
        return arg1.toDouble() >= arg2.toDouble()
    }
}

class LessThanOp : ExprCallable {
    override val name: String = "lt"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Number? = toValue(evaluator, arguments[0])
        val arg2: Number? = toValue(evaluator, arguments[1])
        
        if (arg1 == null || arg2 == null) return false
        
        return arg1.toDouble() < arg2.toDouble()
    }
}

class LessThanOrEqualOp : ExprCallable {
    override val name: String = "lte"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Number? = toValue(evaluator, arguments[0])
        val arg2: Number? = toValue(evaluator, arguments[1])
        
        if (arg1 == null || arg2 == null) return false
        
        return arg1.toDouble() <= arg2.toDouble()
    }
}

class NotOp : ExprCallable {
    override val name: String = "not"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Any? = toValue(evaluator, arguments[0])
        
        if (arg1 == null) return null
        
        return if (arg1 is Boolean) !arg1 else null
    }
}

class OrOp : ExprCallable {
    override val name: String = "or"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Any? = toValue(evaluator, arguments[0])
        val arg2: Any? = toValue(evaluator, arguments[1])
        
        return if (arg1 is Boolean && arg2 is Boolean) {
            arg1 || arg2
        } else {
            arg1 ?: arg2
        }
    }
}

class AndOp : ExprCallable {
    override val name: String = "and"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg1: Any? = toValue(evaluator, arguments[0])
        val arg2: Any? = toValue(evaluator, arguments[1])
        
        return if (arg1 is Boolean && arg2 is Boolean) {
            arg1 && arg2
        } else {
            null
        }
    }
}
