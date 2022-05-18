package me.dmdev.myteamplayer

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

open class MyTeamPlayerActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Hello, Android TV!"
        setContentView(textView)
    }
}