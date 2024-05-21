package br.com.saggion.scrcpytools.util

import br.com.saggion.scrcpytools.domain.Device
import javafx.scene.control.TextArea
import java.io.File

object ADB {
    private val adb = File("${Constants.TEMP_DIRECTORY}scrcpy-tools/scrcpy-win64-v2.4/adb.exe").absolutePath
    var textArea: TextArea? = null

    fun listDevices(): List<Device> {
        var pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("devices")
        pb.command().add("-l")
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val devices =
                bufferedReader.readLines().drop(1).filter { it.isNotEmpty() }.map { line ->
                    val serial = line.substring(0, 23).trim()
                    pb = ProcessBuilder()
                    pb.command().add(adb)
                    pb.command().add("-s")
                    pb.command().add(serial)
                    pb.command().add("shell")
                    pb.command().add("getprop")
                    pb.command().add("ro.product.model")
                    logCommand(pb.command())
                    val hardware = String(pb.start().inputStream.readBytes()).uppercase().trim()

                    pb = ProcessBuilder()
                    pb.command().add(adb)
                    pb.command().add("-s")
                    pb.command().add(serial)
                    pb.command().add("shell")
                    pb.command().add("getprop")
                    pb.command().add("ro.product.manufacturer")
                    logCommand(pb.command())
                    val maker = String(pb.start().inputStream.readBytes()).uppercase().trim()
                    Device(maker, hardware, serial)
                }
            return devices
        }
    }

    fun listPackages(device: Device): List<String> {
        val pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("pm")
        pb.command().add("list")
        pb.command().add("packages")
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val packages =
                bufferedReader.readLines().filter { it.isNotEmpty() }.map { line ->
                    line.substring(8)
                }
            return packages
        }
    }

    fun start(
        device: Device,
        packageName: String,
    ) {
        if (isPackageRunning(device, packageName)) {
            forceStop(device, packageName)
        }

        var pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("dumpsys")
        pb.command().add("package")
        pb.command().add("|")
        pb.command().add("grep")
        pb.command().add("-i")
        pb.command().add(packageName)
        pb.command().add("|")
        pb.command().add("grep")
        pb.command().add("Activity")
        logCommand(pb.command())

        val task = pb.start().inputStream.bufferedReader().readLine().trim().split(" ")[1]

        pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("am")
        pb.command().add("start")
        pb.command().add("-n")
        pb.command().add(task)
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
    }

    fun forceStop(
        device: Device,
        packageName: String,
    ) {
        val pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("am")
        pb.command().add("force-stop")
        pb.command().add(packageName)
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
    }

    fun clearData(
        device: Device,
        packageName: String,
    ) {
        val pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("pm")
        pb.command().add("clear")
        pb.command().add(packageName)
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
    }

    fun uninstall(
        device: Device,
        packageName: String,
    ) {
        val pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("pm")
        pb.command().add("uninstall")
        pb.command().add("--user")
        pb.command().add("0")
        pb.command().add(packageName)
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
    }

    fun startShellConsole(device: Device) {
        val pb = ProcessBuilder()
        pb.command().add("cmd")
        pb.command().add("/k")
        pb.command().add("start")
        pb.command().add("cmd")
        pb.command().add("/c")
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        logCommand(pb.command())

        pb.start()
    }

    private fun isPackageRunning(
        device: Device,
        packageName: String,
    ): Boolean {
        val pb = ProcessBuilder()
        pb.command().add(adb)
        pb.command().add("-s")
        pb.command().add(device.serial)
        pb.command().add("shell")
        pb.command().add("pidof")
        pb.command().add(packageName)
        logCommand(pb.command())

        val process = pb.start()
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val lines = bufferedReader.readLines()
            return lines.isNotEmpty()
        }
    }

    private fun logCommand(command: List<String>) {
        textArea?.appendText(command.joinToString(" ").replace(adb, "adb").plus("\n"))
    }
}
