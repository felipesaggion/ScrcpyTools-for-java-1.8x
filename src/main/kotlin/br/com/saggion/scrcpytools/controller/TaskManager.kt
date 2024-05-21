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
    lateinit var listViewPackages: ListView<String>

    @FXML
    lateinit var buttonStart: Button

    @FXML
    lateinit var buttonForceStop: Button

    @FXML
    lateinit var buttonClearData: Button

    @FXML
    lateinit var buttonUninstall: Button

    @FXML
    lateinit var buttonReload: Button

    override fun initialize(
        location: URL?,
        resources: ResourceBundle?,
    ) {
        loadPackages()
    }

    private fun loadPackages() {
        val packages = ADB.listPackages(DataHolder.instance.device).sorted()
        listViewPackages.items.clear()
        listViewPackages.items.addAll(packages)
    }

    @FXML
    fun buttonStartOnAction() {
        if (!isPackageSelected()) {
            Alert(AlertType.WARNING, "Select a package to perform this action").show()
            return
        }
        val packageName = listViewPackages.selectionModel.selectedItem
        ADB.start(DataHolder.instance.device, packageName)
    }

    @FXML
    fun buttonForceStopOnAction() {
        if (!isPackageSelected()) {
            Alert(AlertType.WARNING, "Select a package to perform this action").show()
            return
        }
        val packageName = listViewPackages.selectionModel.selectedItem
        ADB.forceStop(DataHolder.instance.device, packageName)
    }

    @FXML
    fun buttonClearDataOnAction() {
        if (!isPackageSelected()) {
            Alert(AlertType.WARNING, "Select a package to perform this action").show()
            return
        }
        val packageName = listViewPackages.selectionModel.selectedItem
        ADB.clearData(DataHolder.instance.device, packageName)
    }

    @FXML
    fun buttonUninstallOnAction() {
        if (!isPackageSelected()) {
            Alert(AlertType.WARNING, "Select a package to perform this action").show()
            return
        }
        val packageName = listViewPackages.selectionModel.selectedItem
        ADB.uninstall(DataHolder.instance.device, packageName)
        loadPackages()
    }

    @FXML
    fun buttonReloadOnAction() {
        loadPackages()
    }

    private fun isPackageSelected(): Boolean = listViewPackages.selectionModel.selectedItem != null
}
