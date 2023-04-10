package me.dmdev.myteamplayer.ui

import js.core.jso
import mui.material.*
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import mui.system.responsive
import react.FC
import react.Props

inline fun <I> objectOf(
    jsonObject: I = js("new Object()").unsafeCast<I>(),
    writer: I.() -> Unit
): I {
    writer(jsonObject)
    return jsonObject
}

val App = FC<Props> {

    val theme = createTheme(
        jso {
            palette = jso {
                mode = PaletteMode.dark
                primary = objectOf {
                    main = MyTeamPlayer.Colors.yellow
                }
            }
        }
    )

    ThemeProvider {
        this.theme = theme
        CssBaseline()
        Stack {
            direction = responsive(StackDirection.column)
            Header()
            Toolbar()
            Content()
        }
    }
}