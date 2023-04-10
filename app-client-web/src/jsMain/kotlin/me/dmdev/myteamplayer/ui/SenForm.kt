package me.dmdev.myteamplayer.ui

import csstype.AlignItems
import mui.material.*
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode

val SendForm = FC<Props> {
    Stack {
        direction = responsive(StackDirection.row)
        spacing = responsive(2)
        sx {
            alignItems = AlignItems.center
        }
        TextField {
            label = ReactNode("YouTube link")
            size = Size.small
        }
        Button {
            variant = ButtonVariant.contained
            size = Size.small
            +"Send"
        }
    }
}