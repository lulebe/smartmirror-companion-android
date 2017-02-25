package de.lulebe.smartmirror.intro

import agency.tango.materialintroscreen.SlideFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.lulebe.smartmirror.R




internal class LocalWifiSlide : SlideFragment() {

    private var wifiConnected = false
    private var receiverRegistered = false

    private val wifiReceiver = object: BroadcastReceiver() {
        override fun onReceive (ctx: Context, intent: Intent) {
            val conMan = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = conMan.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected && netInfo.type == ConnectivityManager.TYPE_WIFI) {
                val wifiMan = ctx.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiMan.connectionInfo
                Log.d("WIFI", wifiInfo.ssid)
                if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.ssid) && !wifiInfo.ssid.trim().equals("<unknown ssid>", true)) {
                    connectedToWifi(wifiInfo.ssid)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentSsid = getCurrentSsid(context)
        if (currentSsid != null && !currentSsid.trim().equals("<unknown ssid>", true))
            connectedToWifi(currentSsid)
        else {
            context.registerReceiver(wifiReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
            receiverRegistered = true
        }
    }

    override fun onStop() {
        if (receiverRegistered)
            context.unregisterReceiver(wifiReceiver)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_localwifi_slide, container, false)
        return view
    }

    override fun backgroundColor(): Int {
        return R.color.intro_second
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }

    override fun canMoveFurther(): Boolean {
        return wifiConnected
    }

    override fun cantMoveFurtherErrorMessage(): String {
        return "Not connected to WiFi"
    }

    private fun getCurrentSsid(context: Context): String? {
        var ssid: String? = null
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo ?: return null

        if (networkInfo.isConnected) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = wifiManager.connectionInfo
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.ssid)) {
                ssid = connectionInfo.ssid
            }
        }
        return ssid
    }

    private fun connectedToWifi (ssid: String) {
        wifiConnected = true
        val iv = view!!.findViewById(R.id.image) as ImageView
        val tvTitle = view!!.findViewById(R.id.txt_title) as TextView
        val tvDesc = view!!.findViewById(R.id.txt_desc) as TextView
        iv.setImageResource(R.drawable.wifi)
        tvTitle.text = "WiFi connected"
        tvDesc.text = "Connected to network: " + ssid
    }
}