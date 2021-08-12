package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.Const.Magic.Filename.INTERNAL as DIR_INTERNAL
import com.github.dogsunny.bpehelper.Const.Magic.Filename.EXTERNAL as DIR_EXTERNAL
import com.github.dogsunny.bpehelper.language.maker.util.*
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import org.jetbrains.plugins.scala.lang.psi.api.base.literals.ScStringLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScArgumentExprList
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.EXTERNAL as EXTERNAL_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.INTERNAL as INTERNAL_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.PUBLIC as PUBLIC_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.UNKNOWN as UNKNOWN_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Magic.Filename.AVENUE_CONF as DIR_AVENUE_CONF


/**
 * flow文件跳转到Marker文件
 */
class Flow2XmlLineMarkerProvider : RelatedItemLineMarkerProvider() {

    private val commentRegx = Regex("//\\$([a-zA-Z_]+?)\\.([a-zA-Z_]+?)(\\.with\\([a-zA-Z]+?\\))?")

    override fun collectNavigationMarkers(
        scalaElement: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (!scalaElement.containingFile.name.endsWith(".flow")) return
        markComment(scalaElement, result)
        markInvoke(scalaElement, result)
    }

    private fun markInvoke(
        scalaElement: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (scalaElement !is ScReferenceExpression) return
        if (!scalaElement.text.startsWith("invoke")) return

        val leaf = scalaElement.getNextSiblingOfType<ScArgumentExprList>()
            ?.getChildOfType<ScStringLiteral>()
            ?.getChildOfType<PsiElement>()
            ?:return

        val serviceMessageText = leaf.text
            ?: return
        if (serviceMessageText.length < 5) return
        if (!serviceMessageText.contains('.')) return
        val prueServiceMessageText = serviceMessageText.substring(1..serviceMessageText.length - 2)
        val (serviceName, messageName) = prueServiceMessageText.split(".")
        val (messages, tip) = XmlElementFinder.findMessageTags(scalaElement.project, serviceName, messageName)
        if (messages.isEmpty()) return

        val isInternal = messages.all { it.filePath.contains(DIR_INTERNAL) }
        val isExternal = !isInternal && messages.all { it.filePath.contains(DIR_EXTERNAL) }
        val isPublic = messages.all { it.virtualFile.parent.name == DIR_AVENUE_CONF }
        val icon = when {
            isInternal -> INTERNAL_SERVICE_ICON
            isExternal -> EXTERNAL_SERVICE_ICON
            isPublic -> PUBLIC_SERVICE_ICON
            else -> UNKNOWN_SERVICE_ICON
        }

        val builder = NavigationGutterIconBuilder.create(icon)
            .setTargets(messages)
            .setTooltipText(tip)

        result.add(builder.createLineMarkerInfo(leaf))
    }

    private fun markComment(
        scalaElement: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (scalaElement !is PsiCommentImpl) return
        val groups = commentRegx.matchEntire(scalaElement.text) ?: return
        val values = groups.groupValues
        val service = values[1]
        val message = values[2]

        val (messageTagList, text) = XmlElementFinder.findMessageTags(scalaElement.project, service, message)
        if (messageTagList.isEmpty()) return
        val builder = NavigationGutterIconBuilder.create(Const.Icon.File.DESCRIPTION)
            .setTargets(messageTagList)
            .setTooltipText(text)

        result.add(builder.createLineMarkerInfo(scalaElement))
    }


}