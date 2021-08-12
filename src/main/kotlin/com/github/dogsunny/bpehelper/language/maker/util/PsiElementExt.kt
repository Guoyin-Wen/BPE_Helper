package com.github.dogsunny.bpehelper.language.maker.util

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

// psiTreeUtil
inline fun <reified T : PsiElement>PsiElement.getNextSiblingOfType(): T? =
    PsiTreeUtil.getNextSiblingOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildOfType(): T? =
    PsiTreeUtil.getChildOfType(this, T::class.java)

val PsiElement.virtualFile
    get() = containingFile.virtualFile

val PsiElement.filePath
    get() = virtualFile.path
