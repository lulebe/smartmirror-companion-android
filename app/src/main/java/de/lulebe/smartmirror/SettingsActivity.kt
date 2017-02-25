package de.lulebe.smartmirror

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import de.lulebe.smartmirror.data.Settings
import de.lulebe.smartmirror.mirrorcomm.MirrorHTTP
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onItemSelectedListener

class SettingsActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mirrorHttp: MirrorHTTP? = null
    private var mirrorSettings: Settings? = null


    private var gapiClient: GoogleApiClient? = null
    private var gapiConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.extras.containsKey("address") && intent.extras.getString("address") != null)
            finish()
        mirrorHttp = MirrorHTTP(intent.extras.getString("address"))
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val languageAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        languageAdapter.addAll("EN", "DE")
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setting_language.adapter = languageAdapter
        setting_language.onItemSelectedListener {
            mirrorSettings?.let {
                it.language = setting_language.selectedItem as String
            }
        }

        setting_location.onClick {
            if (gapiConnected) {
                val loc = LocationServices.FusedLocationApi.getLastLocation(gapiClient)
                mirrorSettings?.location = Settings.Location(loc.latitude, loc.longitude)
                setting_location_info.text = loc.latitude.toString() + ", " + loc.longitude.toString()
            }
        }

        fab.setOnClickListener {
            saveSettings()
        }
        gapiClient = GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build()
    }

    override fun onStart() {
        gapiClient?.connect()
        loadSettings()
        super.onStart()
    }

    override fun onStop() {
        gapiConnected = false
        gapiClient?.disconnect()
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConnected(p0: Bundle?) {
        gapiConnected = true
    }
    override fun onConnectionSuspended(p0: Int) {}
    override fun onConnectionFailed(p0: ConnectionResult) {}

    private fun loadSettings () {
        content_settings.visibility = View.GONE
        mirrorHttp?.loadSettings {
            mirrorSettings = it
            showSettings()
        }
    }

    private fun showSettings () {
        mirrorSettings?.let {
            content_settings.visibility = View.VISIBLE
            setting_name.setText(it.name)
            val languageSelection: Int
            when (it.language) {
                "DE" -> {
                    languageSelection = 1
                }
                else -> {
                    languageSelection = 0
                }
            }
            setting_language.setSelection(languageSelection)
            it.location?.let { loc ->
                setting_location_info.text = loc.lat.toString() + ", " + loc.lng.toString()
            }
            setting_sleeptimer.setText(it.sleepTimer.toString())
            setting_autohidefeed.isChecked = it.autoHideFeed
            setting_googleapikey.setText(it.googleAPIKey)
            setting_googlespeechid.setText(it.googleSpeechId)
            setting_googlespeechkey.setText(Gson().toJson(it.googleSpeechKey))
        }
    }

    private fun saveSettings () {
        mirrorSettings?.let {
            it.name = setting_name.text.toString()
            it.sleepTimer = setting_sleeptimer.text.toString().toInt()
            it.autoHideFeed = setting_autohidefeed.isChecked
            it.googleAPIKey = setting_googleapikey.text.toString()
            it.googleSpeechId = setting_googlespeechid.text.toString()
            it.googleSpeechKey = Gson().fromJson(setting_googlespeechkey.text.toString(), Any::class.java)
            mirrorHttp?.saveSettings(it) { success ->
                if (success)
                    finish()
                else
                    contentView?.let { cv ->
                        Snackbar.make(cv, "Error while saving settings", Snackbar.LENGTH_SHORT).show()
                    }
            }
        }
    }

}
