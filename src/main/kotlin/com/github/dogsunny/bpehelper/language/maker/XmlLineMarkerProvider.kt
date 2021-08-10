package com.github.dogsunny.bpehelper.language.maker

import com.github.dogsunny.bpehelper.Const
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement


class XmlLineMarkerProvider : RelatedItemLineMarkerProvider() {

    val regx = Regex("//\\$([a-zA-Z_]+?)\\.([a-zA-Z_]+?)(\\.with\\([a-zA-Z]+?\\))?")

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        // This must be an element with a literal expression as a parent
        if (element !is com.intellij.psi.impl.source.tree.PsiCommentImpl) return
        if (!element.containingFile.name.endsWith(".flow")) return
        val (service, message) = extractServiceMessage(element.text)

        val (messages, text) = XmlUtils.findMessage(element.project, service, message)
        if (messages.isEmpty()) return
        val builder = NavigationGutterIconBuilder.create(Const.Icon.File.DESCRIPTION)
            .setTargets(messages)
            .setTooltipText(text)
            .setEmptyPopupText("没有找到对应接口描述文件")

        result.add(builder.createLineMarkerInfo(element))
    }

    private val prefix = "//$"
    private fun extractServiceMessage(comment: String): Pair<String, String> {
        return comment.trim().removePrefix(prefix).split(".").let { Pair(it[0], it[1]) }
    }

    fun valid(comment: String): Boolean {
        val trim = comment.trim()
        if (!trim.startsWith(prefix)) return false;
        val removePrefix = trim.removePrefix(prefix)
        val array = removePrefix.split(".")
        if (array.size != 2) return false;
        return array[0].isNotBlank() && array[1].isNotBlank();
    }

    private class ServiceMessage() {

    }
}