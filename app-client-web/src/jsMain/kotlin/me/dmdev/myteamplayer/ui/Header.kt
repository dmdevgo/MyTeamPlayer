package me.dmdev.myteamplayer.ui

import csstype.AlignItems
import csstype.JustifyContent
import csstype.px
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Toolbar
import mui.system.sx
import react.FC
import react.Props

val Header = FC<Props> {
    AppBar {
        position = AppBarPosition.fixed
        Toolbar {
            sx {
                justifyContent = JustifyContent.spaceBetween
                alignItems = AlignItems.center
                background = MyTeamPlayer.Colors.dark
                padding = 16.px
            }
            Logo()
            SendForm()
            MainMenu()
        }
    }
}