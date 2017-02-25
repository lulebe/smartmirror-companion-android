package de.lulebe.smartmirror

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.Manifest
import android.os.Bundle
import de.lulebe.smartmirror.intro.LocalWifiSlide
import de.lulebe.smartmirror.intro.MirrorConnectionSlide
import org.jetbrains.anko.defaultSharedPreferences


class IntroActivity : MaterialIntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideBackButton()
        enableLastSlideAlphaExitTransition(true)
        //Initial Slide
        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.intro_first)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.logo)
                .title("Set up your Smartmirror")
                .description("We will guide you through the process")
                .build())
        //location permissions Slide
        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.intro_first)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.crosshairs_gps)
                .title("Location Access")
                .description("Since you can set your mirror location with this app, we need access to your location. It will never get sent or saved anywhere else than on your mirror.")
                .neededPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                .build()
        )
        //local WiFi info Slide
        addSlide(LocalWifiSlide())
        //mirror WiFi info Slide
        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.intro_third)//Color.parseColor("#3F51B5"))
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.wifi)
                .title("Connect Mirror to WiFi")
                .description("Now please connect your Smartmirror to the same WiFi network. To do so, tap and hold the Home button for one second, then use the arrow up and down keys to select a network, type in the password and connect.")
                .build())
        //mirror search Slide
        addSlide(MirrorConnectionSlide())
    }

    override fun onFinish() {
        super.onFinish()
        defaultSharedPreferences.edit().putBoolean("introDone", true).apply()
        finish()
    }
}
