package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.language.maker.po.IdName
import com.github.dogsunny.bpehelper.language.maker.renderer.ListCellRenderer
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
class FlowLineMarkerProvider : RelatedItemLineMarkerProvider() {
    val renderer = ListCellRenderer();
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
        val flows = FlowUtil.findFlowFile(serviceTag.project, serviceIdName, messageIdName).sortedBy { it.name }
        val icon = if (flows.isEmpty()) Const.Icon.File.FLOW_ERROR else Const.Icon.File.FLOW
        val text = if (flows.isEmpty()) "点击跳转" else "没有对应的flow文件"
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

