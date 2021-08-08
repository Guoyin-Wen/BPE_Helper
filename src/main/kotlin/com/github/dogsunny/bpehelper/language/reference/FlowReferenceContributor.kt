package com.github.dogsunny.bpehelper.language.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.FileContentUtil
import com.intellij.util.FileContentUtilCore
import com.intellij.util.ProcessingContext
import org.jetbrains.plugins.scala.conversion.ast.MethodCallExpression
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenType
import org.jetbrains.plugins.scala.lang.psi.api.ScalaPsiElement
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScMethodCall
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScClass
import org.jetbrains.plugins.scala.lang.psi.fake.FakePsiTypeElement
import org.jetbrains.plugins.scala.lang.psi.impl.expr.ScMethodCallImpl
import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.ScClassImpl

// 为一些方法提供引用

class FlowReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider<ScMethodCall> { element, context ->
            arrayOf(FlowMethodReference(element, TextRange(1, (element as ScalaPsiElement).sameElementInContext.toString().length + 1)))
        }
    }

    private inline fun <reified T : PsiElement>PsiReferenceRegistrar.registerReferenceProvider(noinline func: (T, ProcessingContext) -> Array<PsiReference>) {
        registerReferenceProvider(PlatformPatterns.psiElement(T::class.java), createProvider(func))
    }

    companion object {
        fun <T: PsiElement> createProvider(func: (T, ProcessingContext) -> Array<PsiReference>) = object : PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                element as T
                if (!element.containingFile.name.endsWith(".flow")) return emptyArray()
                return func(element, context)
            }
        }
    }
}