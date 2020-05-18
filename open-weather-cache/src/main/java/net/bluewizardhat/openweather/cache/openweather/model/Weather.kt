package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Weather(
  val id: Long?,
  val main: String?,
  val description: String?,
  val icon: String?
)
