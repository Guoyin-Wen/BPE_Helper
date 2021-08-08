package com.github.dogsunny.bpehelper.services

import com.github.dogsunny.bpehelper.Log
import com.github.dogsunny.bpehelper.MyBundle
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
        Log.project = project
        /*val files = FilenameIndex.getFilesByName(project, "", GlobalSearchScope.)
        files.filter { it.element }*/
    }
}
