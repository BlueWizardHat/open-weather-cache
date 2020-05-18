package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Temps(
  val temp: Float?,

  @JsonProperty("feels_like")
  val feelsLike: Float?,

  val pressure: Float?,
  val humidity: Int?,

  @JsonProperty("temp_min")
  val tempMin: Float?,

  @JsonProperty("temp_max")
  val tempMax: Float?,

  @JsonProperty("sea_level")
  val seaLevel: Float?,

  @JsonProperty("grnd_level")
  val groundLevel: Float?
)
