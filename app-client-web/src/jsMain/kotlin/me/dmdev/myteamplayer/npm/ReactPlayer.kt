@file:JsModule("react-player")
@file:JsNonModule

import csstype.Height
import csstype.Width
import react.*

@JsName("default")
external val ReactPlayer: ComponentClass<ReactPlayerProps>

external interface ReactPlayerProps : Props {
    var url: String
    var controls: Boolean
    var playing: Boolean
    var muted: Boolean
    var light: Boolean
    var width: Width
    var height: Height
}
