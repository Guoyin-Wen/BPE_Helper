package com.github.dogsunny.bpehelper.language.maker.util

import com.github.dogsunny.bpehelper.language.maker.po.IdName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.scala.ScalaFileType
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.jetbrains.plugins.scala.lang.psi.api.base.literals.ScStringLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScArgumentExprList
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScMethodCall
import org.jetbrains.plugins.scala.lang.psi.impl.base.ScStringLiteralImpl
import org.jetbrains.plugins.scala.lang.psi.impl.expr.ScArgumentExprListImpl

object FlowElementFinder {

    fun findFlowFile(project: Project, serviceInf: IdName, messageInf: IdName? = null): List<ScalaFile> {
        //val fileNameRegx = getFileNameRegx(serviceInf, messageInf)
        val commentRegx = getCommentRegx(serviceInf, messageInf)
        val psiManager = PsiManager.getInstance(project)
        return FileTypeIndex.getFiles(ScalaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
            //.filter { fileNameRegx.matches(it.name.lowercase()) }
            .filter { it.name.lowercase().endsWith(".flow") }
            .mapNotNull { psiManager.findFile(it) }
            .filterIsInstance<ScalaFile>()
            .filter { scalaFile ->
                scalaFile.getChildrenOfType<PsiComment>()
                    ?.map { it.lowerCaseText.trim() }
                    ?.any { commentRegx.matches(it) }?: false
/*                PsiTreeUtil.findChildrenOfType(it, PsiComment::class.java)
                .any { comment -> commentRegx.matches(comment.text.trim().lowercase()) }*/
            }

    }

    fun findInvoke(project: Project, serviceInf: IdName, messageInf: IdName?): List<ScStringLiteral> {
        val psiManager = PsiManager.getInstance(project)

        return FileTypeIndex.getFiles(ScalaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
            .filter { it.name.endsWith(".flow") }
            .mapNotNull { psiManager.findFile(it) }
            .filterIsInstance<ScalaFile>()
            .flatMap { it.getChildrenOfTypeAsList<ScMethodCall>() }
            .asSequence()
            .filter { it.lowerCaseText.startsWith("invoke") }
            .mapNotNull { it.getChildOfType<ScArgumentExprList>() }
            .mapNotNull { it.getChildOfType<ScStringLiteral>() }
            .filter { it.lowerCaseText.startsWith("\"${serviceInf.name}.") }
            .filter { messageInf == null || it.lowerCaseText.endsWith(".${messageInf.name}\"") }
            .toList()
    }

    private fun getFileNameRegx(serviceInf: IdName, messageInf: IdName?): Regex {
        val serviceId = serviceInf.id
        val messageId = messageInf?.id ?: "[0-9]+?"
        val messageName = messageInf?.name?:"[a-z]+?"
        val pattern = "${serviceId}_${messageId}_${messageName}\\.flow"
        return Regex(pattern)
    }

    private fun getCommentRegx(serviceInf: IdName, messageInf: IdName?): Regex {
        val serviceName = serviceInf.name.lowercase()
        val messageId = messageInf?.id ?: "[0-9]+?"
        val messageName = messageInf?.name?.lowercase()?:"[a-zA-Z]+?"
        val pattern = "//\\$${serviceName}\\.${messageName}(\\.with\\([a-zA-Z]+?\\))?"
        return Regex(pattern)
    }


}