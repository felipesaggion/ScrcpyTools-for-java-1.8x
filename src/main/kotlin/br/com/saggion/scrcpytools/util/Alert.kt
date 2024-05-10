package br.com.saggion.scrcpytools.util

import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.Stage

class Alert(type: AlertType = AlertType.INFORMATION, content: String) : Alert(type) {
    init {
        this.title = Constants.APP_TITLE
        this.contentText = content
        val stage = dialogPane.scene.window as Stage
        stage.icons.add(Image(javaClass.getResourceAsStream("${Constants.BASE_PACKAGE}/icon.png")))
    }
}
