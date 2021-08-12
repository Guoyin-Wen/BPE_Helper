package com.github.dogsunny.bpehelper.language.maker.util

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

// psiTreeUtil
inline fun <reified T : PsiElement>PsiElement.getNextSiblingOfType(): T? =
    PsiTreeUtil.getNextSiblingOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildOfType(): T? =
    PsiTreeUtil.getChildOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildrenOfType(): Array<out T>? =
    PsiTreeUtil.getChildrenOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildrenOfTypeAsList(): MutableList<T> =
    PsiTreeUtil.getChildrenOfTypeAsList(this, T::class.java)

/**
 * 貌似会深度查找，孩子的孩子
 */
inline fun <reified T : PsiElement>PsiElement.findChildOfType(): T? =
    PsiTreeUtil.findChildOfType(this, T::class.java)

val PsiElement.virtualFile: VirtualFile
    get() = containingFile.virtualFile

val PsiElement.filePath: String
    get() = virtualFile.path
