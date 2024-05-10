package br.com.saggion.scrcpytools.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream

object ZipUtils {

    fun unzip(zipFilePath: String, destDir: String) {
        val dir = File(destDir)
        if (!dir.exists()) dir.mkdirs()
        val fis: FileInputStream
        val buffer = ByteArray(1024)
        try {
            fis = FileInputStream(zipFilePath)
            val zis = ZipInputStream(fis)
            var ze = zis.getNextEntry()
            while (ze != null) {
                val fileName = ze.name
                val newFile = File(destDir + File.separator + fileName)
                println("Unzipping to " + newFile.absolutePath)
                if (ze.isDirectory) {
                    newFile.mkdirs()
                    ze = zis.getNextEntry()
                    continue
                }
                File(newFile.getParent()).mkdirs()
                val fos = FileOutputStream(newFile)
                var len: Int
                while (zis.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
                zis.closeEntry()
                ze = zis.getNextEntry()
            }
            zis.closeEntry()
            zis.close()
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
