package com.dev.finxflow.utils

import android.content.Context
import android.os.Environment
import com.dev.finxflow.data.model.Expense
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun export(context: Context, expenses: List<Expense>): String? {
        return try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: context.filesDir
            val fileName = "FinXFlow_Expenses_${System.currentTimeMillis()}.csv"
            val file = File(dir, fileName)

            FileWriter(file).use { writer ->
                writer.append("ID,Name,Category,Amount,Date,Description\n")
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                expenses.forEach { expense ->
                    val safeName = "\"${expense.name.replace("\"", "'")}\""
                    val safeDesc = "\"${expense.description.replace("\"", "'")}\""
                    writer.append("${expense.id},$safeName,${expense.category},${expense.amount},${dateFormat.format(Date(expense.date))},$safeDesc\n")
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}