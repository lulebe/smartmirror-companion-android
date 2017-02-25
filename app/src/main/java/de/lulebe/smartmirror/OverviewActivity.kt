package de.lulebe.smartmirror

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import de.lulebe.smartmirror.data.Settings
import de.lulebe.smartmirror.mirrorcomm.Discovery
import de.lulebe.smartmirror.mirrorcomm.MirrorHTTP
import de.lulebe.smartmirror.ui.ActiveModulesAdapter
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.onClick

class OverviewActivity : AppCompatActivity() {

    private var usedManual = false
    private var discovery: Discovery? = null
    private var address: String? = null
    private var mirrorHTTP: MirrorHTTP? = null
    private var mirrorSettings: Settings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        discovery = Discovery(this, { address, name ->
            if (!usedManual)
                runOnUiThread {
                    this.address = address
                    mirrorHTTP = MirrorHTTP(address)
                    mirrorFound(name)
                }
        }, {
            if (!usedManual)
                (findViewById(R.id.loading_text) as TextView).text = resources.getString(R.string.searching_long)
        })
        if (!defaultSharedPreferences.getBoolean("introDone", false)) {
            startActivity(Intent(this, IntroActivity::class.java))
        }
        findViewById(R.id.fab).onClick {
            val i = Intent(this, AddModuleActivity::class.java)
            i.putExtra("address", address)
            startActivity(i)
        }
    }

    override fun onResume() {
        super.onResume()
        discovery?.start()
    }

    override fun onPause() {
        super.onPause()
        discovery?.stop()
    }

    private fun mirrorFound (name: String) {
        supportActionBar!!.title = name
        (findViewById(R.id.loading_text) as TextView).text = resources.getString(R.string.found_loading_settings)
        mirrorHTTP!!.loadSettings {
            mirrorSettings = it
            settingsLoaded()
        }
    }

    private fun settingsLoaded () {
        findViewById(R.id.fab).visibility = View.VISIBLE
        findViewById(R.id.loading).visibility = View.GONE
        findViewById(R.id.loaded).visibility = View.VISIBLE
        (findViewById(R.id.ip) as TextView).text = "Address: " + address
        findViewById(R.id.settings).onClick {
            val i = Intent(this, SettingsActivity::class.java)
            i.putExtra("address", address)
            startActivity(i)
        }
        val moduleList = findViewById(R.id.active_module_list) as RecyclerView
        val lm = LinearLayoutManager(this)
        moduleList.layoutManager = lm
        moduleList.adapter = ActiveModulesAdapter(mirrorSettings!!.modules, {
            mirrorHTTP!!.saveSettings(mirrorSettings!!) {
                mirrorFound(mirrorSettings!!.name)
            }
        }, {
            val i = Intent(this, ModuleActivity::class.java)
            i.putExtra("address", address)
            i.putExtra("moduleIndex", it)
            startActivity(i)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.overview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.screenon -> {
                mirrorHTTP?.toggleScreen {
                    if (it)
                        Toast.makeText(this, "mirror was turned on/off", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.reboot -> {
                mirrorHTTP?.reboot {
                    if (it)
                        Toast.makeText(this, "mirror is rebooting", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.manual -> {
                manualIPEntry()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun manualIPEntry () {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_ip, null, false)
        val et = view.findViewById(R.id.et_ip) as EditText
        AlertDialog.Builder(this)
                .setTitle("Manual IP mode")
                .setView(view)
                .setNegativeButton(android.R.string.cancel, {di, i ->
                    di.cancel()
                })
                .setPositiveButton(android.R.string.ok, {di, i ->
                    val ip = et.text.toString() + ":3000"
                    usedManual = true
                    address = ip
                    mirrorHTTP = MirrorHTTP(ip)
                    mirrorFound("Manual Mode")
                    di.dismiss()
                })
                .show()
    }
}
