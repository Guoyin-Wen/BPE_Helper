package com.github.dogsunny.bpehelper.language.maker

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl
import javax.swing.Icon


class XmlLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        // This must be an element with a literal expression as a parent
        if (element !is com.intellij.psi.impl.source.tree.PsiCommentImpl) return
        if (!element.containingFile.name.endsWith(".flow")) return
        val (service, message) = extractServiceMessage(element.text)

        // $orderservice.QueryAccountTradeOrNot
        //$orderservice.QueryAccountTradeOrNot
        // The literal expression must start with the Simple language literal expression

        // Get the Simple language property usage
/*        val project: Project = element.getProject()
        val properties: List<SimpleProperty> = SimpleUtil.findProperties(project, possibleProperties)
        if (properties.size > 0) {
            // Add the property to a collection of line marker info

        }*/

        val builder = NavigationGutterIconBuilder.create(IconLoader.getIcon("/META-INF/pluginIcon.svg"))
            //.setTargets(properties)
            .setTooltipText("Navigate to Simple language property")
        result.add(builder.createLineMarkerInfo(element))
    }

    private val prefix = "//$"
    fun extractServiceMessage(comment: String): Pair<String, String> {
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