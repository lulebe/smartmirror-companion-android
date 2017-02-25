package de.lulebe.smartmirror.intro

import agency.tango.materialintroscreen.SlideFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.lulebe.smartmirror.R
import de.lulebe.smartmirror.mirrorcomm.Discovery
import org.jetbrains.anko.runOnUiThread


class MirrorConnectionSlide : SlideFragment() {

    private var mirrorConnected = false
    private var discovery: Discovery? = null


    override fun onAttach(ctx: Context) {
        super.onAttach(ctx)
        discovery = Discovery(ctx, { address, name ->
            ctx.runOnUiThread {
                connectedToMirror(name)
            }
        }, {
            if (isResumed)
                (view?.findViewById(R.id.txt_desc) as TextView).text = resources.getString(R.string.searching_long)
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed)
            discovery?.start()
        else
            discovery?.stop()
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint)
            discovery?.start()
    }

    override fun onPause() {
        super.onPause()
        discovery?.stop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_mirrorconnection_slide, container, false)
        (view.findViewById(R.id.anim_container) as ViewGroup).addView(SearchingMirrorView(context))
        return view
    }

    override fun backgroundColor(): Int {
        return R.color.intro_fourth
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }

    override fun canMoveFurther(): Boolean {
        return mirrorConnected
    }

    override fun cantMoveFurtherErrorMessage(): String {
        return "Not connected to Mirror"
    }

    private fun connectedToMirror (name: String) {
        mirrorConnected = true
        (view!!.findViewById(R.id.anim_container) as ViewGroup).removeAllViews()
        view!!.findViewById(R.id.anim_container).visibility = View.GONE
        view!!.findViewById(R.id.image).visibility = View.VISIBLE
        val tvTitle = view!!.findViewById(R.id.txt_title) as TextView
        val tvDesc = view!!.findViewById(R.id.txt_desc) as TextView
        tvTitle.text = "Connected to Smartmirror"
        tvDesc.text = "Successfully connected to the following mirror: " + name
    }
}