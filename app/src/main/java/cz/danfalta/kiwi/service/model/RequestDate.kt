package cz.danfalta.kiwi.service.model

import java.text.SimpleDateFormat
import java.util.*

data class RequestDate(val date: Date) {

    constructor() : this(Date())
    constructor(time: Long) : this(Date(time))

    companion object {
        const val DATE_FORMAT = "dd/MM/YYYY"
    }

    override fun toString(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(this.date)
    }
}