package com.digia.digiaexpr.std

import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.datetime.DateTimeOperations
import com.digia.digiaexpr.std.iterable.IterableOperations
import com.digia.digiaexpr.std.json.JsonOperations
import com.digia.digiaexpr.std.logical.LogicalOperations
import com.digia.digiaexpr.std.math.MathOperations
import com.digia.digiaexpr.std.string.StringOperations
import com.digia.digiaexpr.std.util.toValue
import com.ibm.icu.text.DecimalFormat;


object StdLibFunctions {
    val functions: Map<String, ExprCallable> = mapOf(
        *LogicalOperations.functions.toList().toTypedArray(),
        *MathOperations.functions.toList().toTypedArray(),
        *StringOperations.functions.toList().toTypedArray(),
        *JsonOperations.functions.toList().toTypedArray(),
        *DateTimeOperations.functions.toList().toTypedArray(),
        *IterableOperations.functions.toList().toTypedArray(),
        "numberFormat" to NumberFormatOp(),
        "toInt" to ToIntOp(),
        "isEmpty" to IsEmptyOp(),
        "length" to LengthOp(),
        "strLength" to LengthOp()
    )
}

class IsEmptyOp : ExprCallable {
    override val name: String = "isEmpty"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg: Any? = toValue(evaluator, arguments.first())
        
        return when (arg) {
            is Number -> arg.toDouble() == 0.0
            is Boolean -> arg
            is String -> arg.isEmpty()
            is List<*> -> arg.isEmpty()
            is Map<*, *> -> arg.isEmpty()
            null -> true
            else -> false
        }
    }
}

class LengthOp : ExprCallable {
    override val name: String = "length"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size != arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val arg: Any? = toValue(evaluator, arguments.first())
        if (arg == null) return null
        
        return when (arg) {
            is String -> arg.length
            is List<*> -> arg.size
            is Map<*, *> -> arg.size
            else -> null
        }
    }
}


class NumberFormatOp : ExprCallable {
    override fun arity(): Int = 2


    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size > arity()) {
            return "Incorrect argument size"
        }

        val number = toValue<Number>(evaluator, arguments[0])
        val arg1 = if (arguments.size > 1) arguments[1] else null
        val format = arg1?.let { toValue<String>(evaluator, it) } ?: "#,##,###"

        return DecimalFormat(format).format(number)
    }

    override val name: String
        get() = "numberFormat"
}

class ToIntOp : ExprCallable {
    override val name: String = "toInt"
    
    override fun arity(): Int = 1
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arity() != arguments.size) {
            throw IllegalArgumentException("Incorrect argument size")
        }
        
        val value: Any? = toValue(evaluator, arguments[0])
        
        return when (value) {
            is String -> {
                if (value.startsWith("0x")) {
                    value.substring(2).toIntOrNull(16)
                } else {
                    value.toDoubleOrNull()?.toInt()
                }
            }
            is Number -> value.toInt()
            else -> null
        }
    }
}
