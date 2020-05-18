package net.bluewizardhat.openweather.cache.openweather.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Clouds(
  val all: Int?
)
