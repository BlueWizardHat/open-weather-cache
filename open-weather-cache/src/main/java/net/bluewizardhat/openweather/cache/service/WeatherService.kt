package net.bluewizardhat.openweather.cache.service

import mu.KotlinLogging
import net.bluewizardhat.openweather.cache.openweather.OpenWeatherMapClient
import net.bluewizardhat.openweather.cache.openweather.model.CurrentWeather
import net.bluewizardhat.openweather.cache.redis.WeatherRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WeatherService @Autowired constructor(
  private val weatherRepository: WeatherRepository,
  private val openWeatherMapClient: OpenWeatherMapClient
) {
  private val log = KotlinLogging.logger {}

  @GetMapping("/weather/location/{locationId}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun currentWeather(@PathVariable("locationId") locationId: Int): CurrentWeather? {
    log.info { "Requesting current weather for $locationId" }
    val result = weatherRepository.getCurrentWeather(locationId)
    log.debug { "Current weather at $locationId: $result" }
    return result
  }

  @GetMapping("/api/forecast", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun forecast(): Map<String, Any> {
    log.info { "Requesting forecast" }
    return openWeatherMapClient.getForecast(2172797)
  }
}
