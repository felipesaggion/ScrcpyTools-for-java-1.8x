package br.com.saggion.scrcpytools.controller

import br.com.saggion.scrcpytools.domain.Device
import br.com.saggion.scrcpytools.domain.Settings
import br.com.saggion.scrcpytools.util.ADB
import br.com.saggion.scrcpytools.util.Alert
import br.com.saggion.scrcpytools.util.Constants
import br.com.saggion.scrcpytools.util.DataHolder
import br.com.saggion.scrcpytools.util.TextInputDialog
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.util.ResourceBundle
import kotlin.concurrent.thread

class Main : Initializable {
    @FXML
    private lateinit var tableViewDevices: TableView<Device>

    @FXML
    private lateinit var tableColumnMaker: TableColumn<Device, String>

    @FXML
    private lateinit var tableColumnHardware: TableColumn<Device, String>

    @FXML
    private lateinit var tableColumnSerial: TableColumn<Device, String>

    @FXML
    private lateinit var buttonMirrorScreen: Button

    @FXML
    private lateinit var buttonRecordScreen: Button

    @FXML
    private lateinit var buttonTaskManager: Button

    @FXML
    private lateinit var buttonReloadDevices: Button

    @FXML
    private lateinit var checkboxAlwaysOnTop: CheckBox

    @FXML
    private lateinit var checkboxFullscreen: CheckBox

    private var settings = Settings()
    private val runtime = Runtime.getRuntime()
    private val scrcpy = File("${Constants.TEMP_DIRECTORY}scrcpy-tools/scrcpy-win64-v2.4/scrcpy.exe").absolutePath

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        tableColumnHardware.setCellValueFactory { SimpleStringProperty(it.value.hardware) }
        tableColumnMaker.setCellValueFactory { SimpleStringProperty(it.value.maker) }
        tableColumnSerial.setCellValueFactory { SimpleStringProperty(it.value.serial) }
        checkboxAlwaysOnTop.isSelected = settings.alwaysOnTop
        checkboxFullscreen.isSelected = settings.fullScreen
        loadDevices()
    }

    private fun loadDevices() {
        tableViewDevices.items.clear()
        tableViewDevices.items.addAll(ADB.listDevices())
    }

    @FXML
    fun buttonMirrorScreenOnAction() {
        if (!isDeviceSelected()) {
            Alert(AlertType.WARNING, "Select a device to mirror the screen").show()
            return
        }
        lockControls(true)
        val device = tableViewDevices.selectionModel.selectedItem
        val title = "${device.maker}(${device.hardware}) ${device.serial}"
        thread {
            val command = StringBuilder()
            command
                .append(scrcpy)
                .append(" --serial=${device.serial}")
                .append(" --power-off-on-close")
                .append(" --stay-awake")
                .append(" --window-title=\"$title\"")
            if (settings.alwaysOnTop) {
                command.append(" --always-on-top")
            }
            if (settings.fullScreen) {
                command.append(" --fullscreen")
            }
            val process = runtime.exec(command.toString())
            process.errorStream.bufferedReader().use { bufferedReader ->
                bufferedReader.readLines().forEach { println(it) }
            }
            process.waitFor()
            lockControls(false)
        }
    }

    @FXML
    fun buttonRecordScreenOnAction() {
        if (!isDeviceSelected()) {
            Alert(AlertType.WARNING, "Select a device to record the screen").show()
            return
        }
        lockControls(true)
        val device = tableViewDevices.selectionModel.selectedItem
        val fileName = "${device.maker}(${device.hardware}) ${device.serial}"
        val textInputDialog = TextInputDialog(fileName)
        textInputDialog.headerText = "Input the file name"
        textInputDialog.showAndWait()
        if (textInputDialog.result.isNullOrBlank()) {
            lockControls(false)
            return
        }
        thread {
            val command = StringBuilder()
            command
                .append(scrcpy)
                .append(" -serial=${device.serial}")
                .append(" --power-off-on-close")
                .append(" --stay-awake")
                .append(" --window-title=\"$fileName\"")
                .append(" --record-format=mp4")
                .append(" --record=\"${textInputDialog.result}.mp4\"")
            if (settings.alwaysOnTop) {
                command.append(" --always-on-top")
            }
            if (settings.fullScreen) {
                command.append(" --fullscreen")
            }
            val process = runtime.exec(command.toString())
            process.errorStream.bufferedReader().use { bufferedReader ->
                bufferedReader.readLines().forEach { println(it) }
            }
            process.waitFor()
            lockControls(false)
        }
    }

    @FXML
    fun buttonReloadDevicesOnAction() {
        loadDevices()
    }

    @FXML
    fun checkboxAlwaysOnTopOnAction() {
        settings = settings.copy(
            alwaysOnTop = !settings.alwaysOnTop,
        )
    }

    @FXML
    fun checkboxFullscreenOnAction() {
        settings = settings.copy(
            fullScreen = !settings.fullScreen,
        )
    }

    @FXML
    fun buttonTaskManagerOnAction() {
        if (!isDeviceSelected()) {
            Alert(AlertType.WARNING, "Select a device to record the screen").show()
            return
        }
        val device = tableViewDevices.selectionModel.selectedItem
        DataHolder.instance.device = device
        val stage = Stage()
        val fxmlLoader = FXMLLoader(javaClass.getResource("${Constants.BASE_PACKAGE}/task-manager.fxml"))
        stage.icons.add(Image(javaClass.getResourceAsStream("${Constants.BASE_PACKAGE}/icon.png")))
        stage.title = Constants.APP_TITLE
        stage.scene = Scene(fxmlLoader.load())
        stage.show()
    }

    private fun lockControls(lock: Boolean) {
        buttonMirrorScreen.isDisable = lock
        buttonRecordScreen.isDisable = lock
        buttonReloadDevices.isDisable = lock
        buttonTaskManager.isDisable = lock
        tableViewDevices.isDisable = lock
        checkboxAlwaysOnTop.isDisable = lock
        checkboxFullscreen.isDisable = lock
    }

    private fun isDeviceSelected(): Boolean = tableViewDevices.selectionModel.selectedItem != null
}
