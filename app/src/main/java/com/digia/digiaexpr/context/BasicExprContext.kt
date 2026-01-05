package com.digia.digiaexpr.context

/** A basic implementation of ExprContext that can be used as-is or extended. */
class BasicExprContext(
        override val name: String = "",
        variables: Map<String, Any?>,
        enclosing: ExprContext? = null
) : ExprContext() {

    private var _enclosing: ExprContext? = enclosing
    private val _variables: MutableMap<String, Any?> = variables.toMutableMap()

    override var enclosing: ExprContext?
        get() = _enclosing
        set(value) {
            _enclosing = value
        }

    override fun getValue(key: String): Pair<Boolean, Any?> {
        if (_variables.containsKey(key)) {
            return Pair(true, _variables[key])
        }
        return _enclosing?.getValue(key) ?: Pair(false, null)
    }
}
