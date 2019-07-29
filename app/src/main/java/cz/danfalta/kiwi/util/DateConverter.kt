package cz.danfalta.kiwi.util

import java.text.SimpleDateFormat
import java.util.*

open class DateConverter {

    companion object {
        private const val NICE_DATE_PATTERN = "dd/MM/YYYY"
        private const val NICE_DATE_TIME_PATTERN = "dd/MM/YYYY HH:mm"

        fun toNiceDateString(date: Date?): String {
            if (date == null) return ""
            return SimpleDateFormat(NICE_DATE_PATTERN, Locale.getDefault()).format(date)
        }

        fun toNiceDateTimeString(date: Date?): String {
            if (date == null) return ""
            return SimpleDateFormat(NICE_DATE_TIME_PATTERN, Locale.getDefault()).format(date)
        }
    }


}