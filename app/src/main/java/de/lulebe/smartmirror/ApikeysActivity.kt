package de.lulebe.smartmirror

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.os.Bundle
import de.lulebe.smartmirror.intro.MirrorConnectionSlide


class ApikeysActivity : MaterialIntroActivity() {

    var address: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        address = intent.extras.getString("address")
        enableLastSlideAlphaExitTransition(true)
        //Initial Slide
        addSlide(SlideFragmentBuilder()
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.logo)
                .title("Set up your API Keys")
                .description("These are access keys for Google Speech recognition and their maps service, so you can take full advantage of your mirror.")
                .build())
        //mirror WiFi info Slide
        addSlide(SlideFragmentBuilder()
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.wifi)
                .title("Connect Mirror to WiFi")
                .description("Now please connect your Smartmirror to the same WiFi network. To do so, tap and hold the Home button for one second, then use the arrow up and down keys to select a network, type in the password and connect.")
                .build())
        //mirror search Slide
        addSlide(MirrorConnectionSlide())
    }
}
