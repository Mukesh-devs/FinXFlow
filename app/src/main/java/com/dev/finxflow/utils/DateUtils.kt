package com.dev.finxflow.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val monthDisplayFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    /** Today 00:00:00 local midnight */
    fun getStartOfToday(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /** 1st day of current month at 00:00:00 */
    fun getStartOfCurrentMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /** Current time as HH:mm:ss */
    fun getCurrentTime(): String = timeFormat.format(Date())

    /** e.g. "June 2026" */
    fun getCurrentMonthDisplay(): String = monthDisplayFormat.format(Date())

    /** Format a timestamp for UI labels */
    fun formatDate(timestamp: Long): String = displayFormat.format(Date(timestamp))
}