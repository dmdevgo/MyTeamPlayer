package me.dmdev.myteamplayer.ui

import kotlinx.browser.window
import mui.icons.material.AndroidOutlined
import mui.icons.material.GitHub
import mui.icons.material.InfoOutlined
import mui.material.IconButton
import mui.material.Stack
import mui.material.StackDirection
import mui.system.responsive
import react.FC
import react.Props

val MainMenu = FC<Props> {
    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(2)
        IconButton {
            AndroidOutlined()
        }
        IconButton {
            onClick = {
                window.open("https://github.com/dmdevgo/MyTeamPlayer")
            }
            GitHub()
        }
        IconButton {
            InfoOutlined()
        }
    }
}