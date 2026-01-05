package com.digia.digiaexpr

import com.digia.digiaexpr.context.BasicExprContext
import org.junit.Test

class IsoFormatDebugTest {
    
    @Test
    fun debugIsoFormat() {
        val testValue = "2024-06-03T23:42:36Z"
        val result = Expression.eval("\${isoFormat(isoDate, 'Do MMMM')}", 
            BasicExprContext(variables = mapOf("isoDate" to testValue)))
        println("Input: $testValue")
        println("Format: 'Do MMMM'")
        println("Result: '$result'")
        println("Expected: '3rd June'")
    }
    
    @Test
    fun debugIsoFormatLeapYear() {
        val testValue = "2024-02-29T00:00:00Z"
        val result = Expression.eval("\${isoFormat(isoDate, 'Do MMMM')}", 
            BasicExprContext(variables = mapOf("isoDate" to testValue)))
        println("Input: $testValue")
        println("Format: 'Do MMMM'")
        println("Result: '$result'")
        println("Expected: '29th February'")
    }
}
