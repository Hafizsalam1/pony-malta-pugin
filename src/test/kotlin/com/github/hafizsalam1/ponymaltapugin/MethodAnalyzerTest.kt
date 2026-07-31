package com.github.hafizsalam1.ponymaltapugin

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.malta.plugin.analyzer.MethodAnalyzer

class MethodAnalyzerTest : BasePlatformTestCase() {

    fun testDetectsSameClassMethodCallsInFlowGraph() {
        val javaCode = """
            class Demo {
                void root() {
                    helper();
                }

                void helper() {
                }
            }
        """.trimIndent()

        val file = myFixture.configureByText(JavaLanguage.INSTANCE, javaCode)
        val psiClass = file.children
            .filterIsInstance<PsiClass>()
            .firstOrNull()

        assertNotNull(psiClass)

        val rootMethod = psiClass!!.methods.first { it.name == "root" }
        val analyzer = MethodAnalyzer()
        val rootNode = analyzer.analyze(rootMethod)

        assertNotNull(rootNode)
        assertEquals(1, rootNode!!.getChildren().size)
        assertEquals("helper", rootNode.getChildren().first().getMethod().name)
    }
}
