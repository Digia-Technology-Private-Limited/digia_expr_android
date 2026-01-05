package com.digia.digiaexpr.context

/**
 * An abstract class representing the context for expression evaluation.
 * This context provides access to variables and supports hierarchical scoping.
 */
abstract class ExprContext {
    /** The name of this context, useful for debugging and logging. */
    abstract val name: String
    
    /** The enclosing context, if any. Used for hierarchical variable lookup. */
    abstract var enclosing: ExprContext?
    
    /**
     * Retrieves a value from the context.
     *
     * Returns a pair where:
     * - The first element is a boolean indicating if the key was found.
     * - The second element is the value associated with the key, or null if not found.
     *
     * This method should search in the current context first, then in enclosing contexts.
     */
    abstract fun getValue(key: String): Pair<Boolean, Any?>
    
    /**
     * Adds a context to the tail of the current context chain.
     *
     * If this context doesn't have an enclosing context, the new context becomes
     * the enclosing context. Otherwise, it's added to the end of the chain.
     */
    fun addContextAtTail(context: ExprContext) {
        if (enclosing == null) {
            enclosing = context
        } else {
            enclosing?.addContextAtTail(context)
        }
    }
}
