package de.lulebe.smartmirror

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import de.lulebe.smartmirror.api.Registry
import de.lulebe.smartmirror.data.Module
import de.lulebe.smartmirror.data.Settings
import de.lulebe.smartmirror.mirrorcomm.MirrorHTTP
import de.lulebe.smartmirror.ui.RegistryModulesAdapter
import kotlinx.android.synthetic.main.activity_add_module.*
import org.jetbrains.anko.contentView
import us.feras.mdv.MarkdownView

class AddModuleActivity : AppCompatActivity() {

    private var mirrorHttp: MirrorHTTP? = null
    private var mirrorSettings: Settings? = null
    private var registry = Registry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.extras.containsKey("address") && intent.extras.getString("address") != null)
            finish()
        mirrorHttp = MirrorHTTP(intent.extras.getString("address"))
        setContentView(R.layout.activity_add_module)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        list.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        loadSettings()
        super.onStart()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun loadSettings () {
        mirrorHttp?.loadSettings { ms ->
            mirrorSettings = ms
            registry.getModules { modules ->
                val shownModules = modules.filter { rm ->
                    !ms.modules.any { lm ->
                        lm.name == rm.name
                    }
                }.toMutableList()
                list.adapter = RegistryModulesAdapter(shownModules, { module ->
                    val i = shownModules.indexOf(module)
                    shownModules.removeAt(i)
                    list.adapter.notifyItemRemoved(i)
                    val newModule = Module()
                    newModule.name = module.name
                    newModule.versionUrl = module.versionUrl
                    newModule.url = module.buildUrl
                    newModule.version = module.version
                    ms.modules.add(newModule)
                    saveSettings()
                }, {
                    val v = MarkdownView(this)
                    v.loadMarkdown(it.description)
                    AlertDialog.Builder(this)
                            .setView(v)
                            .setCancelable(true)
                            .setTitle(it.name)
                            .show()
                })
            }
        }
    }

    private fun saveSettings () {
        mirrorSettings?.let {
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
