package br.com.saggion.scrcpytools.util

import br.com.saggion.scrcpytools.domain.Device
import br.com.saggion.scrcpytools.domain.Settings
import javafx.scene.control.TextArea
import java.io.File

object SCRCPY {
    private val scrcpy = File("${Constants.TEMP_DIRECTORY}scrcpy-tools/scrcpy-win64-v2.4/scrcpy.exe").absolutePath
    var textArea: TextArea? = null

    fun mirrorScreen(
        device: Device,
        settings: Settings,
    ) {
        val title = "${device.maker}(${device.hardware}) ${device.serial}"
        val pb = ProcessBuilder()
        pb.command().add(scrcpy)
        pb.command().add("--serial")
        pb.command().add(device.serial)
        pb.command().add("--power-off-on-close")
        pb.command().add("--stay-awake")
        pb.command().add("--window-title=$title")
        if (settings.alwaysOnTop) {
            pb.command().add("--always-on-top")
        }
        if (settings.fullScreen) {
            pb.command().add("--fullscreen")
        }
        logCommand(pb.command())

        val process = pb.start()
        process.inputStream.bufferedReader().use { bufferedReader ->
            bufferedReader.readLines().forEach { println(it) }
        }
        process.errorStream.bufferedReader().use { bufferedReader ->
            bufferedReader.readLines().forEach { println(it) }
        }
        process.waitFor()
    }

    fun recordScreen(
        device: Device,
        settings: Settings,
        pathToSave: String,
    ) {
        val title = "${device.maker}(${device.hardware}) ${device.serial}"
        val pb = ProcessBuilder()
        pb.command().add(scrcpy)
        pb.command().add("--serial")
        pb.command().add(device.serial)
        pb.command().add("--power-off-on-close")
        pb.command().add("--stay-awake")
        pb.command().add("--window-title=$title")
        pb.command().add("--record-format=mp4")
        pb.command().add("--record=$pathToSave")
        if (settings.alwaysOnTop) {
            pb.command().add("--always-on-top")
        }
        if (settings.fullScreen) {
            pb.command().add("--fullscreen")
        }
        logCommand(pb.command())

        val process = pb.start()
        process.inputStream.bufferedReader().use { bufferedReader ->
            bufferedReader.readLines().forEach { println(it) }
        }
        process.errorStream.bufferedReader().use { bufferedReader ->
            bufferedReader.readLines().forEach { println(it) }
        }
        process.waitFor()
    }

    private fun logCommand(command: List<String>) {
        textArea?.appendText(command.joinToString(" ").replace(scrcpy, "scrcpy").plus("\n"))
    }
}
