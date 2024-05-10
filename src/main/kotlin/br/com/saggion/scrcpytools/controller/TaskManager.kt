package br.com.saggion.scrcpytools.controller

import br.com.saggion.scrcpytools.util.ADB
import br.com.saggion.scrcpytools.util.Alert
import br.com.saggion.scrcpytools.util.DataHolder
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.ListView
import java.net.URL
import java.util.ResourceBundle

class TaskManager : Initializable {

    @FXML
    lateinit var listViewActivities: ListView<String>

    @FXML
    lateinit var listViewPackages: ListView<String>

    @FXML
    lateinit var buttonRestart: Button

    @FXML
    lateinit var buttonForceStop: Button

    @FXML
    lateinit var buttonClearData: Button

    @FXML
    lateinit var buttonUninstall: Button

    @FXML
    lateinit var buttonReload: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        buttonReloadOnAction()
    }

    private fun loadActivities() {
        val tasks = ADB.listActivities(DataHolder.instance.device).sorted()
        listViewActivities.items.clear()
        listViewActivities.items.addAll(tasks)
    }

    private fun loadPackages() {
        val packages = ADB.listPackages(DataHolder.instance.device).sorted()
        listViewPackages.items.clear()
        listViewPackages.items.addAll(packages)
    }

    @FXML
    fun buttonRestartOnAction() {
        if (!isActivitySelected()) {
            Alert(AlertType.WARNING, "Select an activity to perform this action").show()
            return
        }
        val task = listViewActivities.selectionModel.selectedItem
        ADB.restart(DataHolder.instance.device, task)
        loadActivities()
    }

    @FXML
    fun buttonForceStopOnAction() {
        if (!isActivitySelected()) {
            Alert(AlertType.WARNING, "Select an activity to perform this action").show()
            return
        }
        val task = listViewActivities.selectionModel.selectedItem
        val packageName = task.substring(0, task.indexOf("/"))
        ADB.forceStop(DataHolder.instance.device, packageName)
        loadActivities()
    }

    @FXML
    fun buttonClearDataOnAction() {
        if (!isPackageSelected()) {
            Alert(AlertType.WARNING, "Select a package to perform this action").show()
            return
        }
        val packageName = listViewPackages.selectionModel.selectedItem
        ADB.clearData(DataHolder.instance.device, packageName)
        loadPackages()
        loadActivities()
    }

    @FXML
    fun buttonUninstallOnAction() {
        if (!isPackageSelected()) {
            Alert(AlertType.WARNING, "Select a package to perform this action").show()
            return
        }
        val packageName = listViewPackages.selectionModel.selectedItem
        ADB.uninstall(DataHolder.instance.device, packageName)
        buttonReloadOnAction()
    }

    @FXML
    fun buttonReloadOnAction() {
        loadActivities()
        loadPackages()
    }

    private fun isActivitySelected(): Boolean = listViewActivities.selectionModel.selectedItem != null

    private fun isPackageSelected(): Boolean = listViewPackages.selectionModel.selectedItem != null
}
