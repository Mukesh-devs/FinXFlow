package com.dev.finxflow.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.dev.finxflow.data.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val csvDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val fileNameTimeFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun export(context: Context, expenses: List<Expense>): String {
        val header = "Date,Time,Amount,Category,Description"

        val csvContent = buildString {
            appendLine(header)
            expenses.forEach { exp ->
                val dateStr = csvDateFormat.format(Date(exp.date))
                val safeDesc = exp.name.replace("\"", "\"\"") // escape quotes
                appendLine("$dateStr,${exp.time},${exp.amount},${exp.category},\"$safeDesc\"")
            }
        }

        val fileName = "FinXFlow_Expenses_${fileNameTimeFormat.format(Date())}.csv"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { it.write(csvContent.toByteArray()) }
                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    "Exported to Downloads/$fileName"
                } else {
                    "Failed to create file"
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(downloadsDir, fileName)
                file.writeText(csvContent)
                "Exported to ${file.absolutePath}"
            }
        } catch (e: Exception) {
            "Export failed: ${e.message}"
        }
    }
}