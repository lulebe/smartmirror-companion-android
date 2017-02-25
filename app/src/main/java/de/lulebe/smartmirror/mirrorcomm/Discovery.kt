package de.lulebe.smartmirror.mirrorcomm

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdServiceInfo
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread


class Discovery(val context: Context, val mirrorListener: (address: String, name: String) -> Unit, val overtimeListener: () -> Unit) {

    private var found = false
    private var running = false

    private val discoveryListener = object: DiscoveryListener {

        override fun onDiscoveryStarted(serviceType: String?) {
            Log.d("dns-sd info", "started")
            found = false
            doAsync {
                Thread.sleep(10000)
                if (!found)
                    uiThread {
                        overtimeListener()
                    }
            }
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            Log.d("dns-sd info", "stopped")
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
            Log.d("dns-sd info", "found")
            if (serviceInfo == null) return
            resolveService(serviceInfo)
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
            Log.d("dns-sd info", "lost")
            if (serviceInfo == null) return
        }

        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.d("dns-sd info", "start failed")
        }
        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            Log.d("dns-sd info", "stop failed")
        }

    }

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    fun start() {
        if (running) return
        nsdManager.discoverServices("_http._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        running = true
    }

    fun stop() {
        if (!running) return
        nsdManager.stopServiceDiscovery(discoveryListener)
        running = false
    }

    private fun resolveService (serviceInfo: NsdServiceInfo) {
        if (!running) return
        if (!serviceInfo.serviceName.startsWith("LuLeBe SM")) return
        Log.d("service", serviceInfo.serviceName)
        nsdManager.resolveService(serviceInfo, object: NsdManager.ResolveListener {
            override fun onServiceResolved(resolvedServiceInfo: NsdServiceInfo?) {
                if (resolvedServiceInfo == null) {
                    Log.e("service", "null")
                    return
                }
                Log.d("service", resolvedServiceInfo.serviceName)
                Log.d("service", resolvedServiceInfo.serviceType)
                Log.d("service", resolvedServiceInfo.port.toString())
                Log.d("service", resolvedServiceInfo.host.hostAddress)
                Log.d("service", resolvedServiceInfo.host.hostName)
                found = true
                val address = resolvedServiceInfo.host.hostAddress + ":" + resolvedServiceInfo.port.toString()
                context.runOnUiThread {
                    mirrorListener(address, resolvedServiceInfo.serviceName.removePrefix("LuLeBe SM"))
                }
            }

            override fun onResolveFailed(si: NsdServiceInfo?, errorCode: Int) {
                Log.e("service", si!!.serviceName)
                Log.e("service", errorCode.toString())
                Thread.sleep(1000)
                resolveService(serviceInfo)
            }
        })
    }

}