package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CurrentWeatherGroup(
  @JsonProperty("cnt")
  val count: Int,
  val list: List<CurrentWeather>
)
