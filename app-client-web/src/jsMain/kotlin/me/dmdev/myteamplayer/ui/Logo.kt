package me.dmdev.myteamplayer.ui

import csstype.AlignItems
import csstype.FontFamily
import csstype.JustifyContent
import csstype.pt
import emotion.react.css
import mui.icons.material.MusicVideo
import mui.material.Stack
import mui.material.StackDirection
import mui.material.SvgIcon
import mui.material.SvgIconSize
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML

val Logo = FC<Props> {
    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(2)
        sx {
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
        }
        SvgIcon {
            sx {
                color = MyTeamPlayer.Colors.green
            }
            fontSize = SvgIconSize.large
            MusicVideo()
        }
        ReactHTML.span {
            css {
                fontSize = 18.pt
                color = MyTeamPlayer.Colors.green
                fontFamily = "Roboto Serif, serif".unsafeCast<FontFamily>()
            }
            +"My Team Player"
        }
    }
}