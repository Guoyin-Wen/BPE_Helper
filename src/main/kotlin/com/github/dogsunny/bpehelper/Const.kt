package com.github.dogsunny.bpehelper

import com.intellij.openapi.util.IconLoader

object Const {
    object Icon {
        object File {
            val DESCRIPTION = IconLoader.getIcon("/icon/description_file.svg", this.javaClass)
            val FLOW = IconLoader.getIcon("/icon/flow_file.svg", this.javaClass)
            val FLOW_ERROR = IconLoader.getIcon("/icon/flow_error.svg", this.javaClass)
        }
    }
    object Magic {
        object Filename {
            val AVENUE_CONF = "avenue_conf"
        }
    }
}