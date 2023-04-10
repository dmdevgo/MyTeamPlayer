package me.dmdev.myteamplayer.ui

import csstype.AlignItems
import csstype.JustifyContent
import csstype.px
import mui.material.Box
import mui.material.Grid
import mui.material.GridWrap
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props

val Content = FC<Props> {
    Box {
        sx {
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
        }
        Grid {
            sx {
                padding = 32.px
            }
            spacing = responsive(2)
            container = true
            wrap = GridWrap.wrap
            columns = responsive(4)
            for (i in 1..10) {
                Grid {
                    item = true
                    if (i == 1) {
                        VideoPlayerCard()
                    } else {
                        VideoCard()
                    }
                }
            }
        }
    }
}