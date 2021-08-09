package com.github.dogsunny.bpehelper.language.reference

import com.intellij.patterns.InitialPatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.jetbrains.plugins.scala.injection.ScalaLanguageInjectionSupport
import org.jetbrains.plugins.scala.lang.psi.api.ScalaPsiElement
import org.jetbrains.plugins.scala.lang.psi.api.base.ScReference
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaFileImpl
import org.jetbrains.plugins.scala.patterns.ScalaElementPattern


// 为一些方法提供引用

class FlowReferenceContributor : PsiReferenceContributor() {

    // 无法为方法提供 淦 PsiLanguageInjectionHost
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        // ScalaLanguageInjectionSupport
        val sc = Capture(ScReference::class.java)
        val psiElement =
            PlatformPatterns.psiFile(ScalaFileImpl::class.java)
        registrar.registerReferenceProvider(sc,
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<PsiReference> {
                   /* val literalExpression = element as PsiLiteralExpression
                    val value = if (literalExpression.value is String) literalExpression.value as String? else null*/
                    return PsiReference.EMPTY_ARRAY
                }
            })
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

    class Capture<T : ScalaPsiElement?> : ScalaElementPattern<T, Capture<T>?> {
        constructor(elementClass: Class<T>?) : super(elementClass) {}
        constructor(condition: InitialPatternCondition<T>) : super(condition) {}
    }
}