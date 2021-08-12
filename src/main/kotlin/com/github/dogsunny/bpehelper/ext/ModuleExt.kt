package com.github.dogsunny.bpehelper.ext

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope

// 模块下的所有文件
fun Module.findInAllFiles(fileType: FileType): MutableCollection<VirtualFile> {
    val path = guessModuleDir()!!.path
    return FileTypeIndex.getFiles(fileType, GlobalSearchScope.projectScope(project))
        .filter { it.path.contains(path) }
        .toMutableList()
}
