package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentWeather(
  val coord: Coord?,
  val weather: List<Weather>?,
  val main: Temps,
  val visibility: Int?,
  val wind: Wind?,
  val clouds: Clouds?,

  @JsonProperty("dt")
  val time: Long,

  val sys: Sys?,

  val rain: Rain?,

  @JsonProperty("timezone")
  val secondsFromUtc: Long?,

  @JsonProperty("id")
  val cityId: Int,

  @JsonProperty("name")
  val city: String?
)
