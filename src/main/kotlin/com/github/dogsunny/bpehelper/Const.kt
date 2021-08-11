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
                val EXTERNAL = IconLoader.getIcon("/icon/flag/service/external.svg", this.javaClass)
                val INTERNAL = IconLoader.getIcon("/icon/flag/service/internal.svg", this.javaClass)
                val PUBLIC = IconLoader.getIcon("/icon/flag/service/public.svg", this.javaClass)
                val UNCERTAIN = IconLoader.getIcon("/icon/flag/service/uncertain.svg", this.javaClass)

            }
            object Flow {
                val REFERENCE = IconLoader.getIcon("/icon/flag/flow/reference.svg", this.javaClass)
                val REFERENCE_NONE = IconLoader.getIcon("/icon/flag/flow/reference_none.svg", this.javaClass)
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