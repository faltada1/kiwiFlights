package cz.danfalta.kiwi.service.repository.network

import cz.danfalta.kiwi.service.model.InterestingFlights
import cz.danfalta.kiwi.service.model.RequestDate
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.*

interface FlightService {

    @GET(value = "/flights?v=2&sort=popularity&asc=0&locale=en&daysInDestinationFrom=&daysInDestinationTo=&affilid=&children=0&infants=0&flyFrom=49.2-16.61-250km&to=anywhere&featureName=aggregateResults&typeFlight=oneway&returnFrom=&returnTo=&one_per_date=0&oneforcity=1&wait_for_refresh=0&adults=1")
    fun getPopularFlights(
        @Query("dateFrom") from: RequestDate = RequestDate(),
        @Query("dateTo") to: RequestDate = RequestDate(
            Date().time + 1000L * 60 * 60 * 24 * 30
        ),
        @Query("limit") limit: Int = 45
    ): Call<InterestingFlights>
}