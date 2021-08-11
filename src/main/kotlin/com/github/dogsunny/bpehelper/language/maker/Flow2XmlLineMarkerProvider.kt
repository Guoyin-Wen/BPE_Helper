package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.language.maker.util.XmlElementFinder
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import org.jetbrains.plugins.scala.lang.psi.impl.base.ScStringLiteralImpl
import org.jetbrains.plugins.scala.lang.psi.impl.expr.ScArgumentExprListImpl


class Flow2XmlLineMarkerProvider : RelatedItemLineMarkerProvider() {

    private val commentRegx = Regex("//\\$([a-zA-Z_]+?)\\.([a-zA-Z_]+?)(\\.with\\([a-zA-Z]+?\\))?")

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        // This must be an element with a literal expression as a parent
        if (!element.containingFile.name.endsWith(".flow")) return
        commentMark(element, result)
        invokeMark(element, result)
    }

    private fun invokeMark(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (element !is ScReferenceExpression) return
        if (!element.text.startsWith("invoke")) return
        val argumentExpr = PsiTreeUtil.getNextSiblingOfType(element, ScArgumentExprListImpl::class.java)?:return
        val literal = PsiTreeUtil.getChildOfType(argumentExpr, ScStringLiteralImpl::class.java)?:return
        val leaf = PsiTreeUtil.getChildOfType(literal, PsiElement::class.java)?:return
        val text = leaf.text ?: return
        if (text.length < 3) return
        if (!text.contains('.')) return
        val prueText = text.substring(1..text.length - 2)
        val (serviceName, messageName) = prueText.split(".")
        val (messages, tip) = XmlElementFinder.findMessage(element.project, serviceName, messageName)
        if (messages.isEmpty()) return

        val external = messages.all { it.containingFile.virtualFile.path.contains(Const.Magic.Filename.EXTERNAL) }
        val isInternal = messages.all { it.containingFile.virtualFile.path.contains(Const.Magic.Filename.INTERNAL) }
        val isExternal = !isInternal && external
        val isPublic = messages.all { it.containingFile.virtualFile.parent.name == Const.Magic.Filename.AVENUE_CONF }
        val icon = when {
            isInternal -> Const.Icon.Flag.Service.INTERNAL
            isExternal -> Const.Icon.Flag.Service.EXTERNAL
            isPublic -> Const.Icon.Flag.Service.PUBLIC
            else -> Const.Icon.Flag.Service.UNCERTAIN
        }

        val builder = NavigationGutterIconBuilder.create(icon)
            .setTargets(messages)
            .setTooltipText(tip)
            .setEmptyPopupText("没有找到对应接口描述文件")

        result.add(builder.createLineMarkerInfo(leaf))
    }

    private fun commentMark(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (element !is PsiCommentImpl) return
        if (!commentRegx.matches(element.text)) return
        val groups = commentRegx.matchEntire(element.text) ?: return
        val values = groups.groupValues
        val service = values[1]
        val message = values[2]

        val (messages, text) = XmlElementFinder.findMessage(element.project, service, message)
        if (messages.isEmpty()) return
        val builder = NavigationGutterIconBuilder.create(Const.Icon.File.DESCRIPTION)
            .setTargets(messages)
            .setTooltipText(text)
            .setEmptyPopupText("没有找到对应接口描述文件")

        result.add(builder.createLineMarkerInfo(element))
    }


}