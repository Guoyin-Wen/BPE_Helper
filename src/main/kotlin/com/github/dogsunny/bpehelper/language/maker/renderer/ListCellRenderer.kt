package com.github.dogsunny.bpehelper.language.maker.renderer

import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

class ListCellRenderer : DefaultPsiElementCellRenderer() {
    override fun getContainerText(element: PsiElement?, name: String?): String? {
        return ""
    }
}