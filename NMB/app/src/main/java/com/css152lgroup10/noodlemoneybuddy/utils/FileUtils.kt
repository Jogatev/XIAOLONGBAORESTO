package com.css152lgroup10.noodlemoneybuddy.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

object FileUtils {

    fun exportCsvFile(context: Context, fileName: String, data: List<String>): Result<String> {
        return try {
            val exportDir = File(context.getExternalFilesDir(null), Constants.EXPORT_FOLDER_NAME)
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, fileName)
            val writer = FileWriter(file)
            data.forEach { writer.write("$it\n") }
            writer.flush()
            writer.close()

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
