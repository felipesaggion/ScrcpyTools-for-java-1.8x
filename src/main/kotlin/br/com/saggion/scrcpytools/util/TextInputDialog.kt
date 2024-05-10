package br.com.saggion.scrcpytools.util

import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.stage.Stage

class TextInputDialog(defaultValue: String) : TextInputDialog(defaultValue) {
    init {
        dialogPane.minWidth = 400.0
        val stage = dialogPane.scene.window as Stage
        stage.icons.add(Image(javaClass.getResourceAsStream("${Constants.BASE_PACKAGE}/icon.png")))
    }
}
