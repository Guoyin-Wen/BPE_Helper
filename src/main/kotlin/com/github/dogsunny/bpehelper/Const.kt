package com.github.dogsunny.bpehelper

import com.intellij.openapi.util.IconLoader

object Const {
    object Icon {
        object File {
            val DESCRIPTION = IconLoader.getIcon("/icon/description_file.svg", this.javaClass)
            val FLOW = IconLoader.getIcon("/icon/flow_file.svg", this.javaClass)
            val FLOW_ERROR = IconLoader.getIcon("/icon/flow_error.svg", this.javaClass)
        }
        object Flag {
            object Service {
                val EXTERNAL = IconLoader.getIcon("/icon/flag/external_service.svg", this.javaClass)
                val INTERNAL = IconLoader.getIcon("/icon/flag/internal_service.svg", this.javaClass)
                val PUBLIC = IconLoader.getIcon("/icon/flag/public_service.svg", this.javaClass)
                val UNCERTAIN = IconLoader.getIcon("/icon/flag/uncertain_service.svg", this.javaClass)
            }
        }
    }
    object Magic {
        object Filename {
            const val AVENUE_CONF = "avenue_conf"
            const val EXTERNAL = "external"
            const val INTERNAL = "internal"
        }
    }
}