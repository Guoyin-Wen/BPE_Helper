package com.github.dogsunny.bpehelper

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object Log {
    lateinit var project: Project;

    fun show(msg: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("FlowReference")
            .createNotification(msg, type)
            .notify(project)
    }

    fun error(msg: String) = show(msg, NotificationType.ERROR)
    fun warn(msg: String)  = show(msg, NotificationType.WARNING)
}