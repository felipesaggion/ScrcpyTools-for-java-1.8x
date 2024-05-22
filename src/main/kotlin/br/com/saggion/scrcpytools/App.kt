package br.com.saggion.scrcpytools

import br.com.saggion.scrcpytools.util.Constants
import br.com.saggion.scrcpytools.util.ZipUtils
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import kotlin.system.exitProcess

class App : Application() {
    override fun start(stage: Stage) {
        copyDependenciesToTempDirectory()
        val fxmlLoader = FXMLLoader(javaClass.getResource("main.fxml"))
        stage.icons.add(Image(javaClass.getResourceAsStream("icon.png")))
        stage.title = Constants.APP_TITLE
        stage.scene = Scene(fxmlLoader.load())
        stage.setOnCloseRequest { exitProcess(0) }
        stage.show()
    }

    private fun copyDependenciesToTempDirectory() {
        val tempFolder = File(Constants.TEMP_DIRECTORY, "scrcpy-tools")
        if (!tempFolder.exists()) {
            tempFolder.mkdirs()
        }
        val zipFile = File(Constants.SCRCPY_WIN64_ZIP)
        if (File(tempFolder.absolutePath + File.separator + zipFile.nameWithoutExtension).exists()) {
            return
        }
        val zipInputStream = App::class.java.getResourceAsStream("${Constants.BASE_PACKAGE}/${zipFile.name}")!!
        val zipOutputStream = FileOutputStream(zipFile.name)
        IOUtils.copy(zipInputStream, zipOutputStream)
        zipInputStream.close()
        zipOutputStream.close()
        ZipUtils.unzip(zipFile.name, tempFolder.absolutePath)
        zipFile.delete()
    }
}

fun main() {
    Application.launch(App::class.java)
}
