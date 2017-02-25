package de.lulebe.smartmirror.data


class Settings {

    var name = ""
    var language = "EN"
    var location: Location? = null
    var sleepTimer = 300
    var autoHideFeed = false
    var googleAPIKey: String? = null
    var googleSpeechId: String? = null
    var googleSpeechKey: Any? = null
    val modules: MutableList<Module> = mutableListOf()


    class Location(val lat: Double, val lng: Double)

    fun moveModule (module: Module, position: Int) {
        val i = modules.indexOf(module)
        modules.removeAt(i)
        if (i >= position)
            modules[position] = module
        else
            modules[position-1] = module
    }
    fun moveModuleUp (module: Module) {
        val i = modules.indexOf(module)
        if (i == 0) return
        moveModule(module, i-1)
    }
    fun moveModuleDown (module: Module, position: Int) {
        val i = modules.indexOf(module)
        if (i == modules.lastIndex) return
        moveModule(module, i+1)
    }

}