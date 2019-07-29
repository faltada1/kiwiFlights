package cz.danfalta.kiwi.service.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import cz.danfalta.kiwi.KiwiApplication
import cz.danfalta.kiwi.service.model.Country
import cz.danfalta.kiwi.service.model.Flight
import cz.danfalta.kiwi.service.model.InterestingFlights
import cz.danfalta.kiwi.service.repository.network.RetrofitManager
import cz.danfalta.kiwi.service.util.DataWrapper
import cz.danfalta.kiwi.util.SingletonHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


open class FlightRepository private constructor(application: KiwiApplication) {

    companion object : SingletonHolder<FlightRepository, KiwiApplication>(::FlightRepository) {
        const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
        const val NUMBER_OF_FLIGHTS_PER_DAY = 5
        const val REFRESH_RATE_HOURS = 24
        const val AFTER_MIDNIGHT_UPDATE = true
        const val FLIGHT_ITEMS_LIMIT = 30

        const val PREFS_SHOWED_FLIGHTS = "PREFS_SHOWED_FLIGHTS"
        const val PREFS_SHOWED_FLIGHTS_IDS = "PREFS_SHOWED_FLIGHTS_IDS"
        const val PREFS_CURRENT_IDS = "PREFS_CURRENT_IDS"
        const val PREFS_FLIGHTS_LEFT = "PREFS_FLIGHTS_LEFT"
        const val PREFS_UPDATE_DATE = "PREFS_UPDATE_DATE"
        const val PREFS_FLIGHTS = "PREFS_FLIGHTS"
    }

    //Live data
    open val data = MutableLiveData<DataWrapper<List<Flight>>>()
    open val loading = MutableLiveData<Boolean>()

    private val flightService = RetrofitManager.getInstance(application).flightService
    private val prefs = application.getSharedPreferences(PREFS_SHOWED_FLIGHTS, 0)

    open fun getPopularFlights() {
        val cachedResponse = getCachedResponse()
        val flightsLeft = prefs.getInt(PREFS_FLIGHTS_LEFT, -1)
        val needFreshData = cachedResponse == null || (flightsLeft < NUMBER_OF_FLIGHTS_PER_DAY)
        //Loading cached data
        if (!needFreshData) {
            gotResponse(cachedResponse, true)
            return
        }
        //Loading fresh data
        loading.value = true
        flightService.getPopularFlights(limit = FLIGHT_ITEMS_LIMIT)
            .enqueue(object : Callback<InterestingFlights> {
                override fun onResponse(call: Call<InterestingFlights>, response: Response<InterestingFlights>) {
                    loading.value = false
                    val flights = response.body()
                    gotResponse(flights, false)
                    //Cache fresh response
                    cacheResponse(flights)
                }

                override fun onFailure(call: Call<InterestingFlights>, t: Throwable) {
                    loading.value = false
                    failure(t)
                }
            })
    }

    private fun failure(t: Throwable) {
        data.value = DataWrapper(throwable = t)
    }

    /**
     * Main got response logic.
     * */
    private fun gotResponse(flights: InterestingFlights?, fromCache: Boolean) {
        flights?.data?.let {
            val newData =
                if (fromCache) {
                    //Cached Data
                    if (newBatchNeeded()) {
                        //Create new batch from cached items
                        val showedIds = prefs.getStringSet(PREFS_SHOWED_FLIGHTS_IDS, mutableSetOf())!!
                        getBatch(it.filterNot { item -> showedIds.contains(item.id) })
                    } else {
                        //Use the same daily based items
                        val currentCachedIds = prefs.getStringSet(PREFS_CURRENT_IDS, mutableSetOf())!!
                        it.filter { item -> currentCachedIds.contains(item.id) }
                    }
                } else {
                    prefs.edit().clear().apply()
                    getBatch(it)
                }
            //Update data
            data.value = DataWrapper(data = newData)
        }
    }

    /**
     * Get NUMBER_OF_FLIGHTS_PER_DAY random items from all fights
     * and save theirs id to prevent showing them more than once per day
     * */
    private fun getBatch(it: List<Flight>): List<Flight> {
        val randomData = it.shuffled().take(NUMBER_OF_FLIGHTS_PER_DAY)
        val freeItems = it.size - randomData.size
        val currentIds = mutableSetOf<String>()
        currentIds.addAll(randomData.map { d -> d.id })
        val showedIds = prefs.getStringSet(PREFS_SHOWED_FLIGHTS_IDS, mutableSetOf())!!
        showedIds.addAll(currentIds)

        //Update prefs
        prefs.edit().putStringSet(PREFS_SHOWED_FLIGHTS_IDS, showedIds).apply()
        prefs.edit().putInt(PREFS_FLIGHTS_LEFT, freeItems).apply()
        prefs.edit().putStringSet(PREFS_CURRENT_IDS, currentIds).apply()
        prefs.edit().putString(PREFS_UPDATE_DATE, dateToString(Date())).apply()
        return randomData
    }

    /**
     * Method decides if new batch should be loaded based on current time and last batch update
     */
    private fun newBatchNeeded(): Boolean {
        val now = Date()
        val lastUpdateDateString = prefs.getString(PREFS_UPDATE_DATE, dateToString(Date(Long.MIN_VALUE)))!!
        val lastUpdateDate = stringToDate(lastUpdateDateString)

        val diffMs = abs(now.time - lastUpdateDate.time)
        val diffHours = TimeUnit.HOURS.convert(diffMs, TimeUnit.MILLISECONDS)
        return when {
            //Daily update
            (diffHours >= REFRESH_RATE_HOURS) ->
                true
            //After midnight update check
            (diffHours < REFRESH_RATE_HOURS) && AFTER_MIDNIGHT_UPDATE -> {
                val cal = Calendar.getInstance()
                cal.time = now
                val dayNow = cal.get(Calendar.DAY_OF_MONTH)
                cal.time = lastUpdateDate
                val dayUpdate = cal.get(Calendar.DAY_OF_MONTH)
                dayNow != dayUpdate
            }
            else -> false
        }
    }

    /**
     * Helper methods
     * */
    private fun dateToString(date: Date): String {
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return formatter.format(date)
    }

    private fun stringToDate(string: String): Date {
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return formatter.parse(string)
    }

    private fun cacheResponse(body: InterestingFlights?) {
        body?.let {
            val toJson = Gson().toJson(it)
            prefs.edit().putString(PREFS_FLIGHTS, toJson).apply()
        }
    }

    private fun getCachedResponse(): InterestingFlights? {
        val string = prefs.getString(PREFS_FLIGHTS, null)
        return Gson().fromJson(string, InterestingFlights::class.java)
    }
}