package de.lulebe.smartmirror.api

import com.google.gson.Gson
import de.lulebe.smartmirror.data.RegistryModule
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class Registry {

    private val client = OkHttpClient()

    fun getModules (callback: (modules: Array<RegistryModule>) -> Unit) {
        doAsync {
            val res = client.newCall(Request.Builder().url("http://smm-registry.esy.es/api/modules.php").build()).execute()
            val modules = Gson().fromJson(res.body().string(), Array<RegistryModule>::class.java)
            uiThread {
                callback(modules)
            }
        }
    }

    fun getModule(name: String, callback: (module: RegistryModule?) -> Unit) {
        doAsync {
            val res = client.newCall(Request.Builder().url("http://smm-registry.esy.es/api/modules.php?name="+name).build()).execute()
            val module = Gson().fromJson(res.body().string(), RegistryModule::class.java)
            val resVersion = client.newCall(Request.Builder().url(module?.versionUrl).build()).execute()
            module.version = resVersion.body().string().trim().toInt()
            uiThread {
                callback(module)
            }
        }
    }

    fun getVersion(url: String, callback: (v: Int) -> Unit) {
        doAsync {
            val resVersion = client.newCall(Request.Builder().url(url).build()).execute()
            val version = resVersion.body().string().trim().toInt()
            uiThread {
                callback(version)
            }
        }
    }

}