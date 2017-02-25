package de.lulebe.smartmirror.ui

import android.animation.ObjectAnimator
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.lulebe.smartmirror.R
import de.lulebe.smartmirror.api.Registry
import de.lulebe.smartmirror.data.RegistryModule
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onTouch


class RegistryModulesAdapter(val modules: List<RegistryModule>, val addListener: (module: RegistryModule) -> Unit, val clickListener: (module: RegistryModule) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_MODULE = 1
    private val ITEM_TYPE_BOTTOMSPACE = 2

    private val VERSION_LOADING = 0
    private val VERSION_LOADED = 1

    private val registry = Registry()
    private val loadingVersions = mutableMapOf<Int, Int>()

    override fun getItemCount() = modules.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_MODULE -> {
                val h = holder as VH
                h.tvModuleName.text = modules[position].name
                if (!loadingVersions.containsKey(position) || loadingVersions[position] != VERSION_LOADED)
                    h.tvModuleVersion.visibility = View.INVISIBLE
                h.tvModuleVersion.text = "Version " + modules[position].version.toString()
                if (modules[position].isOfficial) {
                    h.ivOfficial.visibility = View.VISIBLE
                    h.tvOfficial.visibility = View.VISIBLE
                }
                if (!loadingVersions.containsKey(position)) {
                    loadingVersions[position] = VERSION_LOADING
                    registry.getVersion(modules[position].versionUrl) {
                        loadingVersions[position] = VERSION_LOADED
                        modules[position].version = it
                        notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_TYPE_BOTTOMSPACE -> {
                val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80F, parent.resources.displayMetrics).toInt()
                val view = View(parent.context)
                view.layoutParams = ViewGroup.LayoutParams(1, height)
                return BottomVH(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_registry_module, parent, false) as ViewGroup
                return VH(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount-1)
            return ITEM_TYPE_BOTTOMSPACE
        return ITEM_TYPE_MODULE
    }

    inner class VH(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val tvModuleName = itemView.findViewById(R.id.module_name) as TextView
        val tvModuleVersion = itemView.findViewById(R.id.module_version) as TextView
        val btnAdd = itemView.findViewById(R.id.btn_add) as ImageView
        val ivOfficial = itemView.findViewById(R.id.module_official_iv) as ImageView
        val tvOfficial = itemView.findViewById(R.id.module_official_tv) as TextView
        init {
            val elev = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4F, itemView.resources.displayMetrics)
            itemView.onTouch { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    val animator = ObjectAnimator.ofFloat(view, "translationZ", 0f, elev)
                    animator.duration = view.context.resources.getInteger(android.R.integer.config_shortAnimTime) / 2L
                    animator.start()
                }
                else if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                    val animator = ObjectAnimator.ofFloat(view, "translationZ", elev, 0f)
                    animator.duration = view.context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                    animator.start()
                }
                false
            }
            itemView.onClick {
                clickListener(modules[adapterPosition])
            }
            btnAdd.onClick {
                addListener(modules[adapterPosition])
            }
        }
    }

    inner class BottomVH(itemView: View) : RecyclerView.ViewHolder(itemView)

}