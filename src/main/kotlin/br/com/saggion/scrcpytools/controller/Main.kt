package br.com.saggion.scrcpytools.controller

import br.com.saggion.scrcpytools.domain.Device
import br.com.saggion.scrcpytools.domain.Settings
import br.com.saggion.scrcpytools.util.ADB
import br.com.saggion.scrcpytools.util.Alert
import br.com.saggion.scrcpytools.util.Constants
import br.com.saggion.scrcpytools.util.DataHolder
import br.com.saggion.scrcpytools.util.SCRCPY
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
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
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
    private lateinit var buttonShellConsole: Button

    @FXML
    private lateinit var buttonTaskManager: Button

    @FXML
    private lateinit var buttonReloadDevices: Button

    @FXML
    private lateinit var checkboxAlwaysOnTop: CheckBox

    @FXML
    private lateinit var checkboxFullscreen: CheckBox

    @FXML
    private lateinit var textAreaCommands: TextArea

    private var settings = Settings()

    override fun initialize(
        location: URL?,
        resources: ResourceBundle?,
    ) {
        tableColumnHardware.setCellValueFactory { SimpleStringProperty(it.value.hardware) }
        tableColumnMaker.setCellValueFactory { SimpleStringProperty(it.value.maker) }
        tableColumnSerial.setCellValueFactory { SimpleStringProperty(it.value.serial) }
        checkboxAlwaysOnTop.isSelected = settings.alwaysOnTop
        checkboxFullscreen.isSelected = settings.fullScreen
        textAreaCommands.font = Font.font("Monospaced", FontWeight.BOLD, 12.0)
        ADB.textArea = textAreaCommands
        SCRCPY.textArea = textAreaCommands
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
        thread {
            SCRCPY.mirrorScreen(device, settings)
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
        val fileChooser =
            FileChooser().apply {
                title = "Select the file name"
                initialDirectory = File(File("").absolutePath)
                initialFileName = "$fileName.mp4"
                extensionFilters.add(FileChooser.ExtensionFilter("MP4 files", "*.mp4"))
            }
        val file = fileChooser.showSaveDialog(buttonRecordScreen.scene.window)

        if (file == null) {
            lockControls(false)
            return
        }

        val pathToSave =
            if (file.absolutePath.lowercase().endsWith(".mp4")) {
                file.absolutePath
            } else {
                file.absolutePath + ".mp4"
            }

        thread {
            SCRCPY.recordScreen(device, settings, pathToSave)
            lockControls(false)
        }
    }

    @FXML
    fun buttonReloadDevicesOnAction() {
        loadDevices()
    }

    @FXML
    fun buttonShellConsoleOnAction() {
        if (!isDeviceSelected()) {
            Alert(AlertType.WARNING, "Select a device to start the shell console").show()
            return
        }
        val device = tableViewDevices.selectionModel.selectedItem
        ADB.startShellConsole(device)
    }

    @FXML
    fun buttonTaskManagerOnAction() {
        if (!isDeviceSelected()) {
            Alert(AlertType.WARNING, "Select a device to start the task manager").show()
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

    @FXML
    fun checkboxAlwaysOnTopOnAction() {
        settings =
            settings.copy(
                alwaysOnTop = !settings.alwaysOnTop,
            )
    }

    @FXML
    fun checkboxFullscreenOnAction() {
        settings =
            settings.copy(
                fullScreen = !settings.fullScreen,
            )
    }

    private fun lockControls(lock: Boolean) {
        buttonMirrorScreen.isDisable = lock
        buttonRecordScreen.isDisable = lock
        buttonReloadDevices.isDisable = lock
        buttonTaskManager.isDisable = lock
        buttonShellConsole.isDisable = lock
        tableViewDevices.isDisable = lock
        checkboxAlwaysOnTop.isDisable = lock
        checkboxFullscreen.isDisable = lock
    }

    private fun isDeviceSelected(): Boolean = tableViewDevices.selectionModel.selectedItem != null
}
