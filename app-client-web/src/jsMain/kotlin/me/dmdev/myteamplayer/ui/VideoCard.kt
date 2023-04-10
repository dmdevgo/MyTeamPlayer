package me.dmdev.myteamplayer.ui

import csstype.AlignItems
import csstype.JustifyContent
import csstype.px
import kotlinx.browser.window
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props

val VideoCard = FC<Props> {
    Card {
        sx {
            width = 320.px
            background = MyTeamPlayer.Colors.deepDark
        }
        CardMedia {
            sx {
                height = 160.px
            }
            image = "https://i.ytimg.com/vi/SDTZ7iX4vTQ/hqdefault.jpg"
        }
        CardContent {
            Typography {
                variant = TypographyVariant.subtitle2
                +"Foster The People - Pumped Up Kicks (Official Video)"
            }
        }
        CardActions {
            sx {
                justifyContent = JustifyContent.end
                alignItems = AlignItems.center
            }
            Button {
                variant = ButtonVariant.text
                size = Size.small
                +"Open"
                onClick = {
                    window.open("https://youtu.be/SDTZ7iX4vTQ")
                }
            }
        }

    }
}