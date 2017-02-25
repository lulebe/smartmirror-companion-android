package de.lulebe.smartmirror.data


class RegistryModule(
        val id: Int,
        val name: String,
        val description: String,
        val buildUrl: String,
        val versionUrl: String,
        val username: String,
        val dataFields: List<DataField>) {

    var version = 1

    val isOfficial: Boolean
        get() {
            return name.startsWith("smm-")
        }

    class DataField (val name: String, val type: String, val description: String)

}