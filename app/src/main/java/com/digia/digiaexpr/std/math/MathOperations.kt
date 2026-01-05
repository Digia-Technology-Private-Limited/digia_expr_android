package com.digia.digiaexpr.std.math

import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.util.toValue
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

object MathOperations {
    val functions: Map<String, ExprCallable> = mapOf(
        "sum" to SumOp(),
        "mul" to MulOp(),
        "multiply" to MulOp(),
        "diff" to DiffOp(),
        "difference" to DiffOp(),
        "divide" to DivideOp(),
        "modulo" to ModuloOp(),
        "ceil" to CeilOp(),
        "floor" to FloorOp(),
        "abs" to AbsOp(),
        "clamp" to ClampOp()
    )
}

class ClampOp : ExprCallable {
    override val name: String = "clamp"
    
    override fun arity(): Int = 3
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Can only resolve 3 arguments")
        }
        
        val value: Number? = toValue(evaluator, arguments[0])
        val min: Number? = toValue(evaluator, arguments[1])
        val max: Number? = toValue(evaluator, arguments[2])
        
        if (value == null || min == null || max == null) return null
        
        return value.toDouble().coerceIn(min.toDouble(), max.toDouble())
    }
}

class ModuloOp : ExprCallable {
    override val name: String = "modulo"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Can only resolve 2 arguments")
        }
        
        val operand1: Number? = toValue(evaluator, arguments[0])
        val operand2: Number? = toValue(evaluator, arguments[1])
        
        if (operand1 == null || operand2 == null) return null
        
        return operand1.toDouble() % operand2.toDouble()
    }
}

class AbsOp : ExprCallable {
    override val name: String = "abs"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Can only resolve 1 argument")
        }
        
        val arg1: Number? = toValue(evaluator, arguments.first())
        
        return arg1?.let { abs(it.toDouble()) }
    }
}

class FloorOp : ExprCallable {
    override val name: String = "floor"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Can only resolve 1 argument")
        }
        
        val arg1: Number? = toValue(evaluator, arguments.first())
        
        return arg1?.let { floor(it.toDouble()).toInt() }
    }
}

class CeilOp : ExprCallable {
    override val name: String = "ceil"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Can only resolve 1 argument")
        }
        
        val arg1: Number? = toValue(evaluator, arguments.first())
        
        return arg1?.let { ceil(it.toDouble()).toInt() }
    }
}

class SumOp : ExprCallable {
    override val name: String = "sum"
    
    override fun arity(): Int = 255
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        val result = arguments.fold(0.0) { acc, e ->
            val num: Number? = toValue(evaluator, e)
            acc + (num?.toDouble() ?: 0.0)
        }
        return result
    }
}

class MulOp : ExprCallable {
    override val name: String = "multiply"
    
    override fun arity(): Int = 255
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        val result = arguments.fold(1.0) { acc, e ->
            val num: Number? = toValue(evaluator, e)
            acc * (num?.toDouble() ?: 1.0)
        }
        return result
    }
}

class DiffOp : ExprCallable {
    override val name: String = "diff"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val operand1: Number? = toValue(evaluator, arguments[0])
        val operand2: Number? = toValue(evaluator, arguments[1])
        
        if (operand1 == null || operand2 == null) {
            throw IllegalArgumentException("Incorrect call to diff. Operands are null: $operand1, $operand2")
        }
        
        return operand1.toDouble() - operand2.toDouble()
    }
}

class DivideOp : ExprCallable {
    override val name: String = "divide"
    
    override fun arity(): Int = 2
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val operand1: Number? = toValue(evaluator, arguments[0])
        val operand2: Number? = toValue(evaluator, arguments[1])
        
        if (operand1 == null || operand2 == null) {
            throw IllegalArgumentException("Operands are null: $operand1, $operand2")
        }
        
        if (operand2.toDouble() == 0.0) {
            throw ArithmeticException("Division by zero")
        }
        
        return operand1.toDouble() / operand2.toDouble()
    }
}
