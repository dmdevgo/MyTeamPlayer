package me.dmdev.myteamplayer.ui

import ReactPlayer
import csstype.AlignItems
import csstype.JustifyContent
import csstype.px
import mui.icons.material.*
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.useState

val VideoPlayerCard = FC<Props> {
    var isPlaying: Boolean by useState(false)
    var isMuted: Boolean by useState(false)
    Card {
        sx {
            width = 320.px
            background = MyTeamPlayer.Colors.deepDark
        }
        CardMedia {
            ReactPlayer {
                url = "https://youtu.be/SDTZ7iX4vTQ"
                controls = false
                playing = isPlaying
                muted = isMuted
                light = false
                width = 320.px
                height = 160.px
            }
        }
        CardContent {
            Typography {
                variant = TypographyVariant.subtitle2
                +"Foster The People - Pumped Up Kicks (Official Video)"
            }
        }
        CardActions {
            sx {
                justifyContent = JustifyContent.spaceAround
                alignItems = AlignItems.center
            }
            IconButton {
                onClick = {
                    isPlaying = isPlaying.not()
                }
                size = Size.small
                color = IconButtonColor.primary
                if (isPlaying) {
                    Pause()
                } else {
                    PlayArrow()
                }
            }
            IconButton {
                onClick = {
                    isMuted = isMuted.not()
                }
                size = Size.small
                color = IconButtonColor.primary
                if (isMuted) {
                    VolumeOff()
                } else {
                    VolumeUp()
                }
            }
            IconButton {
                onClick = {
                }
                size = Size.small
                color = IconButtonColor.primary
                ThumbUp()
            }
            IconButton {
                onClick = {
                }
                size = Size.small
                color = IconButtonColor.primary
                ThumbDown()
            }
        }
    }
}