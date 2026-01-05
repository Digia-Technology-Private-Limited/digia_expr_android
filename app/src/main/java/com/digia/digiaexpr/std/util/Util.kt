package com.digia.digiaexpr.std.util

import com.digia.digiaexpr.ast.ASTNode
import com.digia.digiaexpr.evaluator.ASTEvaluator

fun <T> toValue(evaluator: ASTEvaluator, obj: Any?): T? {
    if (obj == null) return null
    
    return if (obj is ASTNode) {
        evaluator.eval(obj) as? T
    } else {
        obj as? T
    }
}
