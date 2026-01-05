package com.digia.digiaexpr

import com.digia.digiaexpr.ast.ASTNode
import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.callable.ExprClass
import com.digia.digiaexpr.callable.ExprClassInstance
import com.digia.digiaexpr.context.BasicExprContext
import com.digia.digiaexpr.evaluator.ASTEvaluator
import org.junit.Test
import org.junit.Assert.*

/**
 * Basic Expressions Test Suite
 * Ported from Dart digia_expr tests to ensure exact compatibility
 */
class DigiaExprTest {
    
    @Test
    fun testMathFunctionsSumAndMul() {
        val code = "sum(mul(x,4),y)"
        val context = BasicExprContext(variables = mapOf("x" to 10, "y" to 2))
        val result = Expression.eval(code, context)
        assertEquals(42.0, result)
    }
    
    @Test
    fun testStringConcatenation() {
        val code = "concat('abc', 'xyz')"
        val result = Expression.eval(code, null)
        assertEquals("abcxyz", result)
    }
    
    @Test
    fun testSingleStringInterpolation() {
        val code = "Hello \${aVar}!"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf("aVar" to "World")))
        assertEquals("Hello World!", result)
    }
    
    @Test
    fun testMultipleStringInterpolationCase1() {
        val code = "Hello \${a} & \${b}!"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf("a" to "Alpha", "b" to "Beta")))
        assertEquals("Hello Alpha & Beta!", result)
    }
    
    @Test
    fun testMultipleStringInterpolationCase2() {
        val code = "\${a}"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf("a" to "Alpha", "b" to "Beta")))
        assertEquals("Alpha", result)
    }
    
    @Test
    fun testMultipleStringInterpolationCase3() {
        val code = "\${a}, \${b}"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf("a" to "Alpha", "b" to "Beta")))
        assertEquals("Alpha, Beta", result)
    }
    
    @Test
    fun testAccessFieldFromObject() {
        val code = "Hello \${person.name}!"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf(
            "person" to ExprClassInstance(
                klass = ExprClass(
                    name = "Person",
                    fields = mutableMapOf("name" to "Tushar"),
                    methods = emptyMap()
                )
            )
        )))
        assertEquals("Hello Tushar!", result)
    }
    
    @Test
    fun testExecuteMethodOfObject() {
        val testValue = 10
        val data = mapOf("count" to testValue)
        val code = "\${storage.get('count')}"
        
        val result = Expression.eval(code, BasicExprContext(variables = mapOf(
            "storage" to ExprClassInstance(
                klass = ExprClass(
                    name = "LocalStorage",
                    fields = mutableMapOf(),
                    methods = mapOf(
                        "get" to TestMethod { evaluator, args ->
                            data[evaluator.eval(args.first() as ASTNode)]
                        }
                    )
                )
            )
        )))
        assertEquals(testValue, result)
    }
    
    @Test
    fun testAccessFieldFromNestedObject() {
        val testValue = 10
        val code = "\${sum(a.b.c.d(), a.e)}"
        
        val result = Expression.eval(code, BasicExprContext(variables = mapOf(
            "a" to ExprClassInstance(
                klass = ExprClass(
                    name = "Test",
                    fields = mutableMapOf(
                        "b" to ExprClassInstance(
                            klass = ExprClass(
                                name = "Test",
                                fields = mutableMapOf(
                                    "c" to ExprClassInstance(
                                        klass = ExprClass(
                                            name = "Test",
                                            fields = mutableMapOf(),
                                            methods = mapOf(
                                                "d" to TestMethod { _, _ -> testValue }
                                            )
                                        )
                                    )
                                ),
                                methods = emptyMap()
                            )
                        ),
                        "e" to testValue
                    ),
                    methods = emptyMap()
                )
            )
        )))
        assertEquals((testValue + testValue).toDouble(), result)
    }
    
    @Test
    fun testAccessJsonObjectUsingDotNotation() {
        val code = "\${sum(jsonObject.a.b, jsonObject.a.c)}"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf(
            "jsonObject" to mapOf(
                "a" to mapOf("b" to 10, "c" to 2)
            )
        )))
        assertEquals(12.0, result)
    }
    
    @Test
    fun testJsonGet() {
        val testValue = "https://i.imgur.com/tFUQrOe.png"
        val code = "\${get(dataSource, 'data.liveLearning.img')}"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf(
            "dataSource" to mapOf(
                "data" to mapOf(
                    "liveLearning" to mapOf("img" to testValue)
                )
            )
        )))
        assertEquals(testValue, result)
    }
    
    @Test
    fun testIsEqual() {
        val testValue = true
        val code = "\${eq(10, 10)}"
        val result = Expression.eval(code, null)
        assertEquals(testValue, result)
    }
    
    @Test
    fun testIsNotEqual() {
        val testValue = true
        val code = "\${neq(10, 15)}"
        val result = Expression.eval(code, null)
        assertEquals(testValue, result)
    }
    
    @Test
    fun testIsoFormat() {
        val testValue = "2024-06-03T23:42:36Z"
        val output = "3rd June"
        val result = Expression.eval("\${isoFormat(isoDate, 'Do MMMM')}", 
            BasicExprContext(variables = mapOf("isoDate" to testValue)))
        assertEquals(output, result)
    }
    
    @Test
    fun testIsoFormatLeapYear() {
        val testValue = "2024-02-29T00:00:00Z"
        val output = "29th February"
        val result = Expression.eval("\${isoFormat(isoDate, 'Do MMMM')}", 
            BasicExprContext(variables = mapOf("isoDate" to testValue)))
        assertEquals(output, result)
    }
    
    @Test
    fun testNumberFormat() {
        assertEquals("4,56,786", Expression.eval("\${numberFormat(456786)}", null))
    }
    
    @Test
    fun testCustomNumberFormat1() {
        assertEquals("123,456,789", Expression.eval("\${numberFormat(123456789, '#,###,000')}", null))
    }
    
    @Test
    fun testCustomNumberFormat2() {
        assertEquals("30,000", Expression.eval("\${numberFormat(30000, '##,##,###')}", null))
    }
    
    @Test
    fun testToIntFromInteger() {
        assertEquals(100, Expression.eval("\${toInt(100)}", null))
    }
    
    @Test
    fun testToIntFromFloat() {
        assertEquals(100, Expression.eval("\${toInt(100.1)}", null))
    }
    
    @Test
    fun testToIntFromString() {
        assertEquals(100, Expression.eval("\${toInt('100.1')}", null))
    }
    
    @Test
    fun testToIntFromHex() {
        assertEquals(100, Expression.eval("\${toInt('0x64')}", null))
    }
    
    @Test
    fun testComplexConditionWithNumberFormat() {
        val code = "\${condition(isEqual(a, b), 'Note: NPCI may flag repeat transactions of the same amount as duplicates and might reject them. As a precaution, we will deduct ₹\${numberFormat(b)} from your account.', 'Note: You will receive confirmation emails on each steps')}"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf("a" to 1001, "b" to 1001)))
        assertEquals("Note: NPCI may flag repeat transactions of the same amount as duplicates and might reject them. As a precaution, we will deduct ₹1,001 from your account.", result)
    }
    
    @Test
    fun testStringLength() {
        val code = "\${isEqual(strLength(x), length)}"
        val result = Expression.eval(code, BasicExprContext(variables = mapOf("x" to "hello-world", "length" to 11)))
        assertEquals(true, result)
    }
    
    @Test
    fun testQsEncode() {
        val payload = mapOf(
            "key1" to 11,
            "key2" to "str",
            "key3" to false,
            "key4" to 0,
            "key5" to mapOf("cKey1" to true),
            "key6" to listOf(0, 1),
            "key7" to listOf(
                mapOf("cKey1" to 233),
                mapOf("cKey2" to false)
            )
        )
        val result = Expression.eval("\${qsEncode(payload)}", 
            BasicExprContext(variables = mapOf("payload" to payload)))
        assertEquals("key1=11&key2=str&key3=false&key4=0&key5[cKey1]=true&key6=0&key6=1&key7[cKey1]=233&key7[cKey2]=false", result)
    }
    
    @Test
    fun testIfWithoutElseTruthyCondition() {
        assertEquals(false, Expression.eval("\${if(true, false)}", null))
    }
    
    @Test
    fun testIfWithoutElseFalsyCondition() {
        assertEquals(null, Expression.eval("\${if(false, false)}", null))
    }
    
    @Test
    fun testIfWithElseTruthyCondition() {
        assertEquals(false, Expression.eval("\${if(true, false, true)}", null))
    }
    
    @Test
    fun testIfWithElseFalsyCondition() {
        assertEquals(true, Expression.eval("\${if(false, false, true)}", null))
    }
    
    @Test
    fun testMultiIfWithoutElseFirstTruthy() {
        assertEquals("a", Expression.eval("\${if(true, 'a', true, 'b')}", null))
    }
    
    @Test
    fun testMultiIfWithoutElseFirstFalse() {
        assertEquals("b", Expression.eval("\${if(false, 'a', true, 'b')}", null))
    }
    
    @Test
    fun testMultiIfWithoutElseAllFalse() {
        assertEquals(null, Expression.eval("\${if(false, 'a', false, 'b')}", null))
    }
    
    @Test
    fun testMultiIfWithElseAllFalse() {
        assertEquals("c", Expression.eval("\${if(false, 'a', false, 'b', 'c')}", null))
    }
    
    @Test
    fun testGreaterThanFalse() {
        assertEquals(false, Expression.eval("\${gt(1, 2)}", null))
    }
    
    @Test
    fun testGreaterThanTrue() {
        assertEquals(true, Expression.eval("\${gt(2.1, 1.2)}", null))
    }
    
    @Test
    fun testGreaterThanOrEqualFalse() {
        assertEquals(false, Expression.eval("\${gte(1.2, 2.1)}", null))
    }
    
    @Test
    fun testGreaterThanOrEqualTrue() {
        assertEquals(true, Expression.eval("\${gte(2.1, 2.1)}", null))
    }
    
    @Test
    fun testLessThanTrue() {
        assertEquals(true, Expression.eval("\${lt(1, 2)}", null))
    }
    
    @Test
    fun testLessThanFalse() {
        assertEquals(false, Expression.eval("\${lt(2.1, 1.2)}", null))
    }
    
    @Test
    fun testLessThanOrEqualTrue() {
        assertEquals(true, Expression.eval("\${lte(1.2, 2.1)}", null))
    }
    
    @Test
    fun testLessThanOrEqualTrue2() {
        assertEquals(true, Expression.eval("\${lte(2.1, 2.1)}", null))
    }
    
    @Test
    fun testNotTrueToFalse() {
        assertEquals(false, Expression.eval("\${not(true)}", null))
    }
    
    @Test
    fun testNotFalseToTrue() {
        assertEquals(true, Expression.eval("\${not(false)}", null))
    }
    
    @Test
    fun testLogicalOrFalseTrue() {
        assertEquals(true, Expression.eval("\${or(false, true)}", null))
    }
    
    @Test
    fun testLogicalOrFalseFalse() {
        assertEquals(false, Expression.eval("\${or(false, false)}", null))
    }
    
    @Test
    fun testFallbackValueNullCoalesce() {
        assertEquals("a", Expression.eval("\${or(if(false, false), 'a')}", null))
    }
    
    @Test
    fun testFallbackValueNonNull() {
        assertEquals("b", Expression.eval("\${or('b', 'a')}", null))
    }
    
    @Test
    fun testLogicalAndFalseTrue() {
        assertEquals(false, Expression.eval("\${and(false, true)}", null))
    }
    
    @Test
    fun testLogicalAndTrueTrue() {
        assertEquals(true, Expression.eval("\${and(true, true)}", null))
    }
}

/**
 * Helper class for testing method calls on objects
 * Equivalent to Dart's _TestMethod
 */
class TestMethod(
    private val _arity: Int = 0,
    val f: (ASTEvaluator, List<Any>) -> Any?
) : ExprCallable {
    
    override val name: String = "TestMethod"
    
    override fun arity(): Int = _arity
    
    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        return f(evaluator, arguments)
    }
}
