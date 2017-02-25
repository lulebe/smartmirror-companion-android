package de.lulebe.smartmirror

import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import de.lulebe.smartmirror.api.Registry
import de.lulebe.smartmirror.data.Module
import de.lulebe.smartmirror.data.RegistryModule
import de.lulebe.smartmirror.data.Settings
import de.lulebe.smartmirror.mirrorcomm.MirrorHTTP
import de.lulebe.smartmirror.ui.FieldLayout
import kotlinx.android.synthetic.main.activity_module.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.onClick

class ModuleActivity : AppCompatActivity() {

    private var mirrorHttp: MirrorHTTP? = null
    private var mirrorSettings: Settings? = null
    private val registry = Registry()
    private var mirrorModule: Module? = null
    private var onlineModule: RegistryModule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if ((!intent.extras.containsKey("address") && intent.extras.getString("address") != null) ||
                !intent.extras.containsKey("moduleIndex"))
            finish()
        mirrorHttp = MirrorHTTP(intent.extras.getString("address"))

        setContentView(R.layout.activity_module)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun loadSettings () {
        mirrorHttp?.loadSettings {
            mirrorSettings = it
            mirrorModule = it.modules[intent.extras.getInt("moduleIndex")]
            Toast.makeText(this, mirrorModule?.name, Toast.LENGTH_SHORT).show()
            registry.getModule(mirrorModule!!.name) { rm ->
                onlineModule = rm
                showSettings()
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

    private fun showSettings () {
        mirrorModule?.let { mm ->
            onlineModule?.let { om ->
                module_name.text = mm.name
                module_version_installed.text = mm.version.toString()
                module_version_online.text = om.version.toString()
                module_description.loadMarkdown(om.description, "file:///android_asset/classic.css")
                if (om.isOfficial)
                    module_official.visibility = View.VISIBLE
                val fieldLayout = FieldLayout(this, mm, om)
                fieldLayout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                layout.addView(fieldLayout)
                val dp2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2F, resources.displayMetrics)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    scroller.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                        if (scrollY > 0 && ViewCompat.getElevation(titlebar) == 0F)
                            ViewCompat.setElevation(titlebar, dp2)
                        else if (scrollY == 0 && ViewCompat.getElevation(titlebar) != 0F)
                            ViewCompat.setElevation(titlebar, 0F)
                    }
                fab.onClick {
                    fieldLayout.saveToModule()
                    saveSettings()
                }
            }
        }
    }
}
