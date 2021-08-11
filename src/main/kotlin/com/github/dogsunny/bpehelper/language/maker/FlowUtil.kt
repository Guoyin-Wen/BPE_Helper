package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.language.maker.po.IdName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import org.jetbrains.plugins.scala.ScalaFileType
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.jetbrains.plugins.scala.lang.psi.api.ScalaPsiElement
import org.jetbrains.plugins.scala.lang.psi.api.base.ScReference
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScMethodCall
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import org.jetbrains.plugins.scala.lang.psi.impl.base.ScStringLiteralImpl
import org.jetbrains.plugins.scala.lang.psi.impl.expr.ScArgumentExprListImpl
import org.jetbrains.plugins.scala.lang.psi.impl.expr.ScReferenceImpl
import scala.reflect.ClassTag

object FlowUtil {

//59401_1_Login.flow
/*    fun findFlow(project: Project, serviceInf: IdName, messageInf: IdName? = null): List<PsiComment> {
    val fileNameRegx = getFileNameRegx(serviceInf, messageInf)
    val commentRegx = getCommentRegx(serviceInf, messageInf)
    val psiManager = PsiManager.getInstance(project)
    return FileTypeIndex.getFiles(ScalaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
        .filter {
            val matches = fileNameRegx.matches(it.name.lowercase())
            if (!matches) {
                println("${matches.toString()} - ${it.name.lowercase()}")
            }
            matches
        }
        .mapNotNull { psiManager.findFile(it) }
        .filterIsInstance<ScalaFile>()
        .flatMap { PsiTreeUtil.findChildrenOfType(it, PsiComment::class.java) }
        .filter {
            val matches = commentRegx.matches(it.text.trim())
            if (!matches) {
                println("${matches.toString()} - ${it.text.trim()}")
            }
            matches
        }

    }*/

    fun findFlowFile(project: Project, serviceInf: IdName, messageInf: IdName? = null): List<ScalaFile> {
        val fileNameRegx = getFileNameRegx(serviceInf, messageInf)
        val commentRegx = getCommentRegx(serviceInf, messageInf)
        val psiManager = PsiManager.getInstance(project)
        return FileTypeIndex.getFiles(ScalaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
            .filter { fileNameRegx.matches(it.name.lowercase()) }
            .mapNotNull { psiManager.findFile(it) }
            .filterIsInstance<ScalaFile>()
            .filter {
                PsiTreeUtil.findChildrenOfType(it, PsiComment::class.java)
                .any { comment -> commentRegx.matches(comment.text.trim()) }
            }

    }

    fun findInvoke(project: Project, serviceInf: IdName, messageInf: IdName?): List<PsiElement> {
        val psiManager = PsiManager.getInstance(project)
        return FileTypeIndex.getFiles(ScalaFileType.INSTANCE, GlobalSearchScope.projectScope(project))
            .filter { it.name.endsWith(".flow") }
            .mapNotNull { psiManager.findFile(it) }
            .filterIsInstance<ScalaFile>()
            .flatMap { PsiTreeUtil.getChildrenOfTypeAsList(it, ScMethodCall::class.java) }
            .asSequence()
            .filter { it.text.startsWith("invoke") }
            .mapNotNull {
                val arg = PsiTreeUtil.getChildOfType(it, ScArgumentExprListImpl::class.java)
                if (arg == null) null else Pair(it, arg)

            }
            .mapNotNull {
                val literal = PsiTreeUtil.getChildOfType(it.second, ScStringLiteralImpl::class.java)
                if (literal == null) null else Pair(it.first, literal)
            }
            .filter { it.second.text.contains('.') }
            .filter { it.second.text.length >= 3 }
            .filter { it.second.text.startsWith("\""+serviceInf.name) }
            .filter {
                if (messageInf == null) true
                else it.second.text.endsWith(messageInf.name + "\"")
            }
            .map { it.first }
            .toList()
    }

    private fun getFileNameRegx(serviceInf: IdName, messageInf: IdName?): Regex {
        val serviceId = serviceInf.id
        val messageId = messageInf?.id ?: "[0-9]+?"
        val messageName = messageInf?.name?:"[a-z]+?"
        val pattern = "${serviceId}_${messageId}_${messageName}\\.flow".lowercase()
        return Regex(pattern)
    }

    private fun getCommentRegx(serviceInf: IdName, messageInf: IdName?): Regex {
        val serviceName = serviceInf.name
        val messageId = messageInf?.id ?: "[0-9]+?"
        val messageName = messageInf?.name?:"[a-zA-Z]+?"
        val pattern = "//\\$${serviceName}\\.${messageName}(\\.with\\([a-zA-Z]+?\\))?"
        return Regex(pattern)
    }


}