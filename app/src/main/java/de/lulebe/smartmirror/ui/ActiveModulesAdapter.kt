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
import de.lulebe.smartmirror.data.Module
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onTouch




class ActiveModulesAdapter(val modules: MutableList<Module>, val changeListener: () -> Unit, val clickListener: (index: Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_MODULE = 1
    private val ITEM_TYPE_BOTTOMSPACE = 2

    override fun getItemCount() = modules.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_MODULE -> {
                val h = holder as VH
                h.tvModuleName.text = modules[position].name
                h.tvModuleVersion.text = "Version " + modules[position].version.toString()
                if (modules[position].isOfficial) {
                    h.ivOfficial.visibility = View.VISIBLE
                    h.tvOfficial.visibility = View.VISIBLE
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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_active_module, parent, false) as ViewGroup
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
        val btnRemove = itemView.findViewById(R.id.btn_remove) as ImageView
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
                clickListener(adapterPosition)
            }
            btnRemove.onClick {
                modules.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                changeListener()
            }
        }
    }

    inner class BottomVH(itemView: View) : RecyclerView.ViewHolder(itemView)

}