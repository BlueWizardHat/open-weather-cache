package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Sys(
  val country: String?,
  val sunrise: Long?,
  val sunset: Long?
)
