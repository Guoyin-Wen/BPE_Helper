package com.github.dogsunny.bpehelper.services

import com.github.dogsunny.bpehelper.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
