package com.github.dogsunny.bpehelper.ext

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope

fun Project.findFiles(fileType: FileType): MutableCollection<VirtualFile> {
    return FileTypeIndex.getFiles(fileType, GlobalSearchScope.projectScope(this))
}