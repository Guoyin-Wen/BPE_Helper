package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.language.maker.po.IdName
import com.github.dogsunny.bpehelper.language.maker.renderer.ListCellRenderer
import com.github.dogsunny.bpehelper.language.maker.renderer.XmlLinkedServiceCellRenderer
import com.github.dogsunny.bpehelper.language.maker.util.FlowElementFinder
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag

/**
 * 从Xml跳转的Flow
 */
class Xml2FlowLineMarkerProvider : RelatedItemLineMarkerProvider() {
    private val renderer = ListCellRenderer();
    private val xmlLinkedServiceCellRenderer = XmlLinkedServiceCellRenderer();
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val containingFile = element.containingFile
        if (!containingFile.virtualFile.path.contains(Const.Magic.Filename.AVENUE_CONF)) return
        if (element !is XmlTag) return

        val serviceTag = getServiceTag(element)?:return
        val serviceIdName = tag2IdName(serviceTag)?:return
        val messageTag = getMessageTag(element)
        val messageIdName = messageTag?.let { tag2IdName(it) }
        flowDefinition(serviceTag, serviceIdName, messageIdName, result, element)
        reference(serviceTag, serviceIdName, messageIdName, result, element)
    }

    private fun reference(
        serviceTag: XmlTag,
        serviceIdName: IdName,
        messageIdName: IdName?,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
        element: XmlTag
    ) {
        val flows = FlowElementFinder.findInvoke(serviceTag.project, serviceIdName, messageIdName)
        val icon = if (flows.isEmpty()) Const.Icon.Flag.Flow.REFERENCE_NONE else Const.Icon.Flag.Flow.REFERENCE
        val text = if (flows.isNotEmpty()) "用到此消息的地方" else "没有对应的flow文件"
        val builder = NavigationGutterIconBuilder.create(icon)
            .setTargets(flows)
            .setTooltipText(text)
            .setCellRenderer(xmlLinkedServiceCellRenderer)

        result.add(builder.createLineMarkerInfo(element.firstChild))
    }

    private fun flowDefinition(
        serviceTag: XmlTag,
        serviceIdName: IdName,
        messageIdName: IdName?,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
        element: XmlTag
    ) {
        val flows = FlowElementFinder.findFlowFile(serviceTag.project, serviceIdName, messageIdName).sortedBy {
            it.name.split("_")[1].toIntOrNull()
        }
        val icon = if (flows.isEmpty()) Const.Icon.File.FLOW_ERROR else Const.Icon.File.FLOW
        val text = if (flows.isNotEmpty()) "点击跳转" else "没有对应的flow文件"
        val builder = NavigationGutterIconBuilder.create(icon)
            .setTargets(flows)
            .setTooltipText(text)
            .setCellRenderer(renderer)

        result.add(builder.createLineMarkerInfo(element.firstChild))
    }

    private fun getMessageTag(element: XmlTag): XmlTag? {
        if (element.name == "message") return element
        return null;
    }

    private fun getServiceTag(element: XmlTag): XmlTag? {
        if (element.name == "service") return element;
        if (element.name == "message") {
            val parent = element.parent
            if (parent is XmlTag && parent.name == "service") return parent
        }
        return null;
    }

    private fun tag2IdName(xmlTag: XmlTag): IdName? {
        val id = xmlTag.getAttributeValue("id")?:return null
        val name = xmlTag.getAttributeValue("name")?:return null
        return IdName(id, name)
    }

}

