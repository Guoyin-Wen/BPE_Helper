package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Flow.REFERENCE
import com.github.dogsunny.bpehelper.Const.Icon.Flag.Flow.REFERENCE_NONE
import com.github.dogsunny.bpehelper.language.maker.po.IdName
import com.github.dogsunny.bpehelper.language.maker.renderer.ListCellRenderer
import com.github.dogsunny.bpehelper.language.maker.renderer.XmlLinkedServiceCellRenderer
import com.github.dogsunny.bpehelper.language.maker.util.FlowElementFinder
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import com.github.dogsunny.bpehelper.Const.Icon.File.FLOW as FLOW_ICON
import com.github.dogsunny.bpehelper.Const.Icon.File.FLOW_NONE as FLOW_NONE_ICON

/**
 * 从Xml跳转的Flow
 */
class Xml2FlowLineMarkerProvider : RelatedItemLineMarkerProvider() {
    companion object {
        val DEFINITION_RENDERER = ListCellRenderer()
        private val REFERENCE_RENDERER = XmlLinkedServiceCellRenderer()
    }


    override fun collectNavigationMarkers(
        targetXmlTag: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val xmlFilePath = targetXmlTag.containingFile.virtualFile.path
        if (!xmlFilePath.contains(Const.Magic.Filename.AVENUE_CONF)) return
        if (targetXmlTag !is XmlTag) return

        val serviceIdName = targetXmlTag.getServiceTag()?.tag2IdName()?:return
        val messageIdName = targetXmlTag.getMessageTag()?.run { tag2IdName() }

        val markTarget = targetXmlTag.firstChild
        // 这里两个如果再增加用类重构
        val definitionMarkerBuilder = definitionMarkerBuilder(serviceIdName, messageIdName, targetXmlTag)
        result.add(definitionMarkerBuilder.createLineMarkerInfo(markTarget))

        val referenceMakerBuilder = referenceMarkerBuilder(serviceIdName, messageIdName, targetXmlTag)
        result.add(referenceMakerBuilder.createLineMarkerInfo(markTarget))
    }

    private fun referenceMarkerBuilder(
        serviceInf: IdName,
        messageInf: IdName?,
        xmlTag: XmlTag
    ): NavigationGutterIconBuilder<PsiElement> {
        val methodCallList = FlowElementFinder.findInvoke(xmlTag.project, serviceInf, messageInf)
        val icon = if (methodCallList.isEmpty()) REFERENCE_NONE else REFERENCE
        val text = if (methodCallList.isNotEmpty()) "用到此消息的地方" else "没有对应的flow文件"
        return NavigationGutterIconBuilder.create(icon)
            .setTargets(methodCallList)
            .setTooltipText(text)
            .setCellRenderer(REFERENCE_RENDERER)
    }

    private fun definitionMarkerBuilder(
        serviceIdName: IdName,
        messageIdName: IdName?,
        element: XmlTag
    ): NavigationGutterIconBuilder<PsiElement> {
        val flows = FlowElementFinder.findFlowFile(element.project, serviceIdName, messageIdName).sortedBy {
            it.name.split("_")[1].toIntOrNull()
        }
        val icon = if (flows.isEmpty()) FLOW_NONE_ICON else FLOW_ICON
        val text = if (flows.isNotEmpty()) "点击跳转" else "没有对应的flow文件"
        return NavigationGutterIconBuilder.create(icon)
            .setTargets(flows)
            .setTooltipText(text)
            .setCellRenderer(DEFINITION_RENDERER)
    }

    private fun XmlTag.getMessageTag(): XmlTag?  = if (name == "message") this else null

    private fun XmlTag.getServiceTag(): XmlTag? {
        if (name == "service") return this
        if (name == "message" && parentTag?.name == "service") return parentTag
        return null
    }

    private fun XmlTag.tag2IdName(): IdName? {
        val id = getAttrValueLowerCase("id")?:return null
        val name = getAttrValueLowerCase("name")?:return null
        return IdName(id, name)
    }

    private fun XmlTag.getAttrValueLowerCase(qname: String) = getAttributeValue(qname)?.lowercase()

}

