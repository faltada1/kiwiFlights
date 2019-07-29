package cz.danfalta.kiwi.service.model

import com.google.gson.annotations.SerializedName

data class InterestingFlights(@SerializedName("data") val data: List<Flight>)