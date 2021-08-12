package com.github.dogsunny.bpehelper.language.maker.util

import com.github.dogsunny.bpehelper.Const
import com.github.dogsunny.bpehelper.ext.findFiles
import com.github.dogsunny.bpehelper.ext.findInAllFiles
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.github.dogsunny.bpehelper.Const.Magic.Filename.AVENUE_CONF as DIR_AVENUE_CONF
import com.github.dogsunny.bpehelper.Const.Magic.XmlAttr.NAME as NAME

object XmlElementFinder {

    fun findMessageTags(module: Module, serviceName: String, messageName: String): List<XmlTag> {
        return module.findInAllFiles(XmlFileType.INSTANCE)
            .filter { it.path.contains(DIR_AVENUE_CONF) }
            .mapNotNull { PsiManager.getInstance(module.project).findFile(it) }
            .filterIsInstance<XmlFile>()
            .mapNotNull { it.document }
            .mapNotNull { it.rootTag }
            .filter { it.attrIgnoreCaseEq(NAME, serviceName) }
            .flatMap { it.findSubTags(Const.Magic.XmlNode.MESSAGE).toList() }
            .filterNotNull()
            .filter { it.attrIgnoreCaseEq(NAME, messageName) }
    }

}