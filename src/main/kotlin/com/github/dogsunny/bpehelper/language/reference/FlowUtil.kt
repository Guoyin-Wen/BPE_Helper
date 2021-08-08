package com.github.dogsunny.bpehelper.language.reference

import com.github.dogsunny.bpehelper.Log
import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import okhttp3.internal.notify
import org.jetbrains.plugins.scala.ScalaFileType
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.jetbrains.plugins.scala.lang.psi.api.statements.ScFunctionDeclaration
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.ScNamedElement
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScClass
import kotlin.math.log

object FlowUtil {


    fun getFun(project: Project, key: String): ScFunctionDeclaration? {
        val flowClass = getFlowClass(project)
        val funcArray = PsiTreeUtil.getChildrenOfType(flowClass, ScFunctionDeclaration::class.java)
        val filteredFuncArray = funcArray.filter { (it as ScNamedElement).name() == key }
        if (filteredFuncArray.isEmpty()) {
            Log.error("没有找到 $key 方法")
            return null
        }
        if (filteredFuncArray.size > 1) {
            Log.warn("多个 $key 方法")
        }
        return filteredFuncArray[0]
    }


    // 拿到class
    fun getFlowClass(project: Project): ScClass? {
        val flowClassFile = getFlowClassFile(project)
        flowClassFile?:return null
        val classes :Array<ScClass> = PsiTreeUtil.getChildrenOfType(flowClassFile, ScClass::class.java)
        if (classes.isEmpty()) {
            Log.error("没有找到Flow class")
        }
        return classes[0]
    }

    // 拿到class 相关的文件
    fun getFlowClassFile(project: Project): ScalaFile? {
        val files = FileTypeIndex.getFiles(ScalaFileType.INSTANCE,
            GlobalSearchScope.projectScope(project))
        val flowFile = files.filter { it.name == "Flow.scala" }
        if (flowFile.isEmpty()) {
            Log.error("没有找到Flow scala文件")
            return null
        }
        if (flowFile.size > 1) {
            Log.warn("多个Flow scala文件")
        }
        return PsiManager.getInstance(project).findFile(flowFile[0]) as ScalaFile
    }

}