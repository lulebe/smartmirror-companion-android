package de.lulebe.smartmirror.mirrorcomm

import com.google.gson.Gson
import de.lulebe.smartmirror.data.Settings
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MirrorHTTP (val address: String) {

    private val client = OkHttpClient()

    fun loadSettings (callback: (settings: Settings) -> Unit) {
        doAsync {
            val req = Request.Builder().url("http://$address/settings").build()
            val res = client.newCall(req).execute()
            val settings = Gson().fromJson(res.body().string(), Settings::class.java)
            uiThread {
                callback(settings)
            }
        }
    }

    fun saveSettings (settings: Settings, callback: (success: Boolean) -> Unit) {
        doAsync {
            val JSON = MediaType.parse("application/json; charset=utf-8")

            val body = RequestBody.create(JSON, Gson().toJson(settings, Settings::class.java))
            val req = Request.Builder()
                    .url("http://$address/settings")
                    .post(body)
                    .build()
            val res = client.newCall(req).execute()
            uiThread {
                callback(res.isSuccessful)
            }
        }
    }

    fun toggleScreen (callback: (success: Boolean) -> Unit) {
        doAsync {
            val req = Request.Builder().url("http://$address/misc/screentoggle").build()
            val res = client.newCall(req).execute()
            uiThread {
                callback(res.isSuccessful)
            }
        }
    }

    fun reboot (callback: (success: Boolean) -> Unit) {
        doAsync {
            val req = Request.Builder().url("http://$address/misc/reboot").build()
            val res = client.newCall(req).execute()
            uiThread {
                callback(res.isSuccessful)
            }
        }
    }

}