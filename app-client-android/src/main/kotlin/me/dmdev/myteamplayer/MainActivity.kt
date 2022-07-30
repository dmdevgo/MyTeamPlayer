package me.dmdev.myteamplayer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import me.dmdev.myteamplayer.presentation.main.MainContainer
import me.dmdev.myteamplayer.presentation.main.MainPm
import me.dmdev.myteamplayer.serialization.JsonBundleStateSaver
import me.dmdev.myteamplayer.ui.rememberWindowSizes
import me.dmdev.myteamplayer.ui.screen.MainScreen
import me.dmdev.myteamplayer.ui.theme.MyTeamPlayerTheme
import me.dmdev.premo.PmActivity
import me.dmdev.premo.PmActivityDelegate
import me.dmdev.premo.navigation.handleBack

class MainActivity : PmActivity<MainPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTeamPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowSizes = rememberWindowSizes()
                    MainScreen(delegate.presentationModel, windowSizes)
                }
            }
        }
    }

    override val delegate: PmActivityDelegate<MainPm> = PmActivityDelegate(
        pmActivity = this,
        pmDescription = MainPm.Description,
        pmFactory = MainContainer(PlatformContainerImpl(this)),
        stateSaver = JsonBundleStateSaver()
    )

    override fun onBackPressed() {
        if (delegate.presentationModel.handleBack().not()) {
            super.onBackPressed()
        }
    }
}
