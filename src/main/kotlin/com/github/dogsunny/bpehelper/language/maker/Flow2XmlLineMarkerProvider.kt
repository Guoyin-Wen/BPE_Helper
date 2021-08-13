package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.language.maker.po.IdName
import com.github.dogsunny.bpehelper.Const.Magic.Filename.INTERNAL as DIR_INTERNAL
import com.github.dogsunny.bpehelper.Const.Magic.Filename.EXTERNAL as DIR_EXTERNAL
import com.github.dogsunny.bpehelper.language.maker.util.*
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import com.intellij.psi.xml.XmlTag
import org.jetbrains.plugins.scala.lang.psi.api.base.literals.ScStringLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScArgumentExprList
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScReferenceExpression
import java.util.*
import com.github.dogsunny.bpehelper.Const.Icon.File.DESCRIPTION as DESCRIPTION_ICON
import com.github.dogsunny.bpehelper.Const.Icon.File.DESCRIPTION_NONE as DESCRIPTION_NONE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.EXTERNAL as EXTERNAL_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.INTERNAL as INTERNAL_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.NONE as NONE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.PUBLIC as PUBLIC_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Service.UNKNOWN as UNKNOWN_SERVICE_ICON
import com.github.dogsunny.bpehelper.Const.Magic.Filename.AVENUE_CONF as DIR_AVENUE_CONF


/**
 * flow文件跳转到Marker文件
 */
class Flow2XmlLineMarkerProvider : RelatedItemLineMarkerProvider() {

    private val commentRegx = Regex("//\\$([a-zA-Z_]+?)\\.([a-zA-Z0-9_]+?)(\\.with\\([a-zA-Z]+?\\))?")

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

        val (serviceName, messageName) = Optional.ofNullable(leaf.text)
            .filter { it.length >= 5 }
            .filter { it.contains(".") }
            .map { it.substring(1 until it.lastIndex).lowercase() }
            .map { it.split(".") }
            .orElse(null)
            ?:return

        val messageTags = XmlElementFinder.findMessageTags(scalaElement.module()!!, serviceName, messageName)
        invokeToXml( messageTags, result, leaf)

        // 因为名称和name相同，所以直接用
        val messageId = messageTags[0].attrValue(Const.Magic.XmlAttr.ID)
        val serviceId = messageTags[0].parentTag?.attrValue(Const.Magic.XmlAttr.ID)
        if (messageId != null && serviceId != null) {
            val flows = FlowElementFinder.findFlowFile(
                scalaElement.project,
                IdName(serviceId, serviceName),
                IdName(messageId, messageName)
            ).sortedBy {
                it.name.split("_")[1].toIntOrNull()
            }
            val icon = if (flows.isEmpty()) Const.Icon.File.FLOW_NONE else Const.Icon.File.FLOW
            val text = if (flows.isNotEmpty()) "点击跳转" else "没有对应的flow文件"
            NavigationGutterIconBuilder.create(icon)
                .setTargets(flows)
                .setTooltipText(text)
                .setCellRenderer(Xml2FlowLineMarkerProvider.DEFINITION_RENDERER)
                .createLineMarkerInfo(leaf)
                .let { result.add(it) }
        }
    }

    private fun invokeToXml(
        messageTags: List<XmlTag>,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>,
        leaf: PsiElement
    ) {
        val isTagsNone = messageTags.isEmpty()
        val isInternal = messageTags.all { it.filePath.contains(DIR_INTERNAL) }
        val isExternal = !isInternal && messageTags.all { it.filePath.contains(DIR_EXTERNAL) }
        val isPublic = messageTags.all { it.virtualFile.parent.name == DIR_AVENUE_CONF }
        val icon = when {
            isTagsNone -> NONE_ICON
            isInternal -> INTERNAL_SERVICE_ICON
            isExternal -> EXTERNAL_SERVICE_ICON
            isPublic -> PUBLIC_SERVICE_ICON
            else -> UNKNOWN_SERVICE_ICON
        }
        val show = Const.Icon.Flag.Service.NAME_MAP[icon] ?: "..."
        val builder = NavigationGutterIconBuilder.create(icon)
            .setTargets(messageTags)
            .setTooltipText(show)

        result.add(builder.createLineMarkerInfo(leaf))
    }


    private fun markComment(
        scalaElement: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (scalaElement !is PsiCommentImpl) return

        val (_, serviceName, messageName) = commentRegx
            .matchEntire(scalaElement.text)
            ?.groupValues
            ?: return

        val messageTags = XmlElementFinder.findMessageTags(scalaElement.module()!!, serviceName, messageName)
        val icon = if (messageTags.isEmpty()) DESCRIPTION_NONE_ICON else DESCRIPTION_ICON
        val builder = NavigationGutterIconBuilder.create(icon)
            .setTargets(messageTags)
            .setTooltipText("Navigate to xml")

        result.add(builder.createLineMarkerInfo(scalaElement))
    }


}