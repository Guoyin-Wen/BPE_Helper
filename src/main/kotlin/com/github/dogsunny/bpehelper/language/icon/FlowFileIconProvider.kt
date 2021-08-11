package com.github.dogsunny.bpehelper.language.icon

import com.github.dogsunny.bpehelper.Const
import com.intellij.ide.FileIconProvider
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import org.jetbrains.plugins.scala.ScalaLanguage
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import javax.swing.Icon

class FlowFileIconProvider : FileIconProvider {
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        project?:return null
        val psiFile = PsiManager.getInstance(project).findFile(file)
        psiFile?:return null
        return when (psiFile.language) {
            XMLLanguage.INSTANCE -> xml(psiFile)
            //ScalaLanguage.INSTANCE -> flow(psiFile)
            else -> null
        }
    }

    private fun flow(psiFile: PsiFile): Icon? {
        if (psiFile !is ScalaFile) return null
        if (!psiFile.name.endsWith(".flow")) return null
        return Const.Icon.File.FLOW
    }

    private fun xml(psiFile: PsiFile): Icon? {
        if (psiFile !is XmlFile) return null
        if (!psiFile.virtualFile.path.contains(Const.Magic.Filename.AVENUE_CONF)) return null
        return Const.Icon.File.DESCRIPTION
    }

}