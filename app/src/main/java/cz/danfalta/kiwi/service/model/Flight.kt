package cz.danfalta.kiwi.service.model

data class Flight(val id: String, val countryFrom: Country, val countryTo: Country, val dTime: Long)
