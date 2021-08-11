package com.github.dogsunny.bpehelper.language.maker

import com.intellij.diff.contents.FileDocumentContentImpl
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.*
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import org.jetbrains.plugins.scala.ScalaFileType
import org.jetbrains.uast.test.env.findUElementByTextFromPsi

object XmlUtils {
    fun findXml(project: Project, service: String): VirtualFile? {
        val xml = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project))
            .filter { Regex("${service}_[0-9]+\\.xml").matches(it.name) }
            .filter { it.parent.path.contains("avenue_conf") }
        if (xml.isEmpty()) return null
        return xml[0]
    }

    fun findMessage(project: Project, service: String, message: String): Pair<List<XmlTag>, String> {
        val xmlFile = findXml(project, service) ?: return Pair(emptyList(), "")
        val findFile = PsiManager.getInstance(project).findFile(xmlFile) as XmlFile
        val nodes = (findFile.document
            ?.rootTag
            ?.findSubTags("message")
            ?.filter { it.getAttributeValue("name") == message }
            ?: emptyList())
        val serviceId = findFile.document?.rootTag?.getAttributeValue("id")
        val text = nodes.joinToString(separator = "\n") {
            "service_id: $serviceId message_id:${it.getAttributeValue("id")}"
        }
        return Pair(nodes, text)
    }
}