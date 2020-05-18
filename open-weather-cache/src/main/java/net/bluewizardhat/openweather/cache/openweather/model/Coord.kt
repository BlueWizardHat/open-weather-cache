package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Coord(
  val lon: Float?,
  val lat: Float?
)
