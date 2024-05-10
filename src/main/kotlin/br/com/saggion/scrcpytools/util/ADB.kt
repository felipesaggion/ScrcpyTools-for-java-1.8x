package br.com.saggion.scrcpytools.util

import br.com.saggion.scrcpytools.domain.Device
import java.io.File

object ADB {
    private val adb = File("${Constants.TEMP_DIRECTORY}scrcpy-tools/scrcpy-win64-v2.4/adb.exe").absolutePath
    private val runtime = Runtime.getRuntime()

    fun listDevices(): List<Device> {
        val process = runtime.exec(adb.plus(" devices -l"))
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val devices = bufferedReader.readLines().drop(1).filter { it.isNotEmpty() }.map { line ->
                val serial = line.substring(0, 23).trim()
                val hardware = String(
                    runtime.exec(
                        adb.plus(" -s $serial shell getprop ro.product.model"),
                    ).inputStream.readBytes(),
                ).uppercase().trim()
                val maker = String(
                    runtime.exec(
                        adb.plus(" -s $serial shell getprop ro.product.manufacturer"),
                    ).inputStream.readBytes(),
                ).uppercase().trim()
                Device(maker, hardware, serial)
            }
            return devices
        }
    }

    fun listActivities(device: Device): List<String> {
        val command = adb.plus(" -s ${device.serial} shell dumpsys activity activities | grep \"realActivity=\"")
        val process = runtime.exec(command)
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val runningActivities = bufferedReader.readLines()
                .filter { it.isNotEmpty() }
                .filterIndexed { index, _ -> index % 2 == 0 }
                .map { line ->
                    line.trim().split("=")[1]
                }
            return runningActivities
        }
    }

    fun listPackages(device: Device): List<String> {
        val command = adb.plus(" -s ${device.serial} shell pm list packages")
        val process = runtime.exec(command)
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val packages = bufferedReader.readLines().filter { it.isNotEmpty() }.map { line ->
                line.substring(8)
            }
            return packages
        }
    }

    fun restart(device: Device, task: String) {
        if (isPackageRunning(device, task.substring(0, task.indexOf("/")))) {
            forceStop(device, task.substring(0, task.indexOf("/")))
        }
        val process = runtime.exec(adb.plus(" -s ${device.serial} shell am start -n $task"))
        process.waitFor()
    }

    fun forceStop(device: Device, packageName: String) {
        val process = runtime.exec(adb.plus(" -s ${device.serial} shell am force-stop $packageName"))
        process.waitFor()
    }

    fun clearData(device: Device, packageName: String) {
        val process = runtime.exec(adb.plus(" -s ${device.serial} shell pm clear $packageName"))
        process.waitFor()
    }

    fun uninstall(device: Device, packageName: String) {
        val process = runtime.exec(adb.plus(" -s ${device.serial} shell pm uninstall --user 0 $packageName"))
        process.waitFor()
    }

    private fun isPackageRunning(device: Device, packageName: String): Boolean {
        val process = runtime.exec(adb.plus(" -s ${device.serial} shell pidof $packageName"))
        process.waitFor()
        process.inputStream.bufferedReader().use { bufferedReader ->
            val lines = bufferedReader.readLines()
            return lines.isNotEmpty()
        }
    }
}
