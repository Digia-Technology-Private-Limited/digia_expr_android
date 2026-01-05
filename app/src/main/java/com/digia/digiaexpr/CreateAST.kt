package com.digia.digiaexpr

import com.digia.digiaexpr.ast.ASTNode
import com.digia.digiaexpr.parser.Parser
import com.digia.digiaexpr.scanner.Scanner

object CreateAST {
    fun createAST(source: String): ASTNode {
        val tokens = Scanner(source).scanTokens()
        val ast = Parser(tokens).parse()
        return ast
    }
}

