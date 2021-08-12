package com.github.dogsunny.bpehelper.language.maker.util

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag

// psiTreeUtil
inline fun <reified T : PsiElement>PsiElement.getNextSiblingOfType(): T? =
    PsiTreeUtil.getNextSiblingOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildOfType(): T? =
    PsiTreeUtil.getChildOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildrenOfType(): Array<out T>? =
    PsiTreeUtil.getChildrenOfType(this, T::class.java)

inline fun <reified T : PsiElement>PsiElement.getChildrenOfTypeAsList(): MutableList<T> =
    PsiTreeUtil.getChildrenOfTypeAsList(this, T::class.java)

fun PsiElement.module() = ModuleUtil.findModuleForPsiElement(this)

/*fun <T : PsiElement> PsiElement.getChildOfTypePath(type: Class<T>, vararg path: Class<in PsiElement>): MutableList<T> {

    val iterator: Iterator<Class<in PsiElement>> = path.iterator()
}

private tailrec fun <T : PsiElement>recursionGetChildOfTypePath(
    finalType: Class<T>,
    //tempTypeList: Array<Class<out PsiElement>>,
    findRoot: PsiElement,
    path: Array<Class<out PsiElement>>,
    index: Int): T?
{
    if (index == path.lastIndex) {
        return PsiTreeUtil.getChildOfType(findRoot, finalType)
    }
    val childrenOfType = PsiTreeUtil.getChildrenOfType(findRoot, path[index])
    val
}*/

/**
 * 貌似会深度查找，孩子的孩子
 */
inline fun <reified T : PsiElement>PsiElement.findChildOfType(): T? =
    PsiTreeUtil.findChildOfType(this, T::class.java)

val PsiElement.virtualFile: VirtualFile
    get() = containingFile.virtualFile

val PsiElement.filePath: String
    get() = virtualFile.path

val PsiElement.lowerCaseText: String
    get() = text.lowercase()

// 子类 xml
fun XmlTag.attrEq(attrName: String, value: String): Boolean {
    return getAttributeValue(attrName) == value
}

fun XmlTag.attrIgnoreCaseEq(attrName: String, value: String): Boolean {
    return getAttributeValue(attrName)?.lowercase() == value.lowercase()
}

fun XmlTag.attrValue(name: String): String? {
    return getAttributeValue(name)
}