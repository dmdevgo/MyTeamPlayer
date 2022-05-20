package me.dmdev.myteamplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.widget.TextView


open class MyTeamPlayerActivity : Activity() {

    private val player = MyTeamPlayer()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Hello, Android TV!" + "\n ${getIpAddressInLocalNetwork()}"
        textView.textSize = 48F
        setContentView(textView)
        player.startServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stopServer()
    }

    private fun getIpAddressInLocalNetwork(): String? {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }
}
