package cz.danfalta.kiwi.service.util

data class DataWrapper<T>(val data: T? = null, val throwable: Throwable? = null) {

    fun hasData(): Boolean {
        return data != null && !hasError()
    }

    fun hasError(): Boolean {
        return throwable != null
    }
}