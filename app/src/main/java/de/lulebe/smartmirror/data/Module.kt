package de.lulebe.smartmirror.data

import java.util.*


class Module {
    var name = ""
    var url = ""
    var versionUrl = ""
    var version = 1
    var data = HashMap<String, Any>()

    val isOfficial: Boolean
        get() {
            return name.startsWith("smm-")
        }

}