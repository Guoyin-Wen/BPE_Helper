package com.github.dogsunny.bpehelper.language.maker.renderer

import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.presentation.java.SymbolPresentationUtil

class XmlLinkedServiceCellRenderer : PsiElementListCellRenderer<PsiElement>() {
    override fun getIconFlags(): Int {
        return Iconable.ICON_FLAG_VISIBILITY
    }

    override fun getElementText(element: PsiElement?): String? {
        return SymbolPresentationUtil.getSymbolPresentableText(element!!.containingFile)
    }

    override fun getContainerText(element: PsiElement?, name: String?): String? {
        return element!!.textRange.run { "$startOffset-$endOffset" }
    }
}