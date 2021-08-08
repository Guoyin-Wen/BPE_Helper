package com.github.dogsunny.bpehelper.language.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class FlowMethodReference(psiElement: PsiElement, textRange: TextRange)
    : PsiReferenceBase<PsiElement>(psiElement, textRange) {
    private val key = psiElement.text.substring(textRange.startOffset, textRange.endOffset);

    override fun resolve(): PsiElement? {
        return FlowUtil.getFun(myElement.project, key)
    }
}