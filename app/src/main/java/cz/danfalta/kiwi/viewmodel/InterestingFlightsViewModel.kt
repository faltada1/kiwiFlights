package cz.danfalta.kiwi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import cz.danfalta.kiwi.service.model.Flight
import cz.danfalta.kiwi.service.model.InterestingFlights
import cz.danfalta.kiwi.service.repository.FlightRepository
import cz.danfalta.kiwi.service.util.DataWrapper


class InterestingFlightsViewModel(application: Application) : AndroidViewModel(application) {

    private val flightRepository = FlightRepository.getInstance(getApplication())

    val flightListObservable: LiveData<DataWrapper<List<Flight>>> = flightRepository.data
    val loadingObservable: LiveData<Boolean> = flightRepository.loading

    fun loadData() {
        flightRepository.getPopularFlights()
    }
}