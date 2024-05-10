package br.com.saggion.scrcpytools.util

class Constants {
    companion object {
        const val APP_TITLE = "Screen Copy Tools"
        const val SCRCPY_VERSION = "v2.4"
        const val ADB_VERSION = "v1.0.41"
        const val SCRCPY_WIN64_ZIP = "scrcpy-win64-$SCRCPY_VERSION.zip"
        const val BASE_PACKAGE = "/br/com/saggion/scrcpytools"
        val TEMP_DIRECTORY: String = System.getProperty("java.io.tmpdir")
    }
}
