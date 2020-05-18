package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Rain(
  @JsonProperty("1h")
  val oneHour: Float?,

  @JsonProperty("3h")
  val threeHour: Float?
)
