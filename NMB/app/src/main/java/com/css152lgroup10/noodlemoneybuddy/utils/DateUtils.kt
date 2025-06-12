package com.css152lgroup10.noodlemoneybuddy.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getCurrentDateFormatted(): String {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatDate(input: String, fromFormat: String, toFormat: String): String {
        return try {
            val sdfFrom = SimpleDateFormat(fromFormat, Locale.getDefault())
            val sdfTo = SimpleDateFormat(toFormat, Locale.getDefault())
            val date = sdfFrom.parse(input)
            sdfTo.format(date ?: Date())
        } catch (e: Exception) {
            input
        }
    }
}
