package net.bluewizardhat.openweather.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import net.bluewizardhat.openweather.cache.openweather.OpenWeatherMapClient
import net.bluewizardhat.openweather.cache.openweather.model.CurrentWeather
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class WeatherRepository@Autowired constructor(
  private val redisTemplate: RedisTemplate<Any, Any>,
  private val openWeatherMapClient: OpenWeatherMapClient,
  private val objectMapper: ObjectMapper
) {
  companion object {
    const val locationCachedRedisKey = "locations"
    const val locationForRefreshRedisKey = "locationsForRefresh"
  }

  private val log = KotlinLogging.logger {}

  private val listOperations = redisTemplate.opsForList()
  private val valueOperations = redisTemplate.opsForValue()
  private val lockOperations = redisTemplate.opsForValue()

  init {
    log.info { "Initialized" }
  }

  fun getCurrentWeather(locationId: Int): CurrentWeather? {
    var weather = readFromCache(locationId)

    if (weather != null) {
      log.info { "Weather for $locationId was found in cache" }
      return weather
    }

    val lock = "$locationId.current.lock"
    log.info { "Weather for $locationId was not found in cache, attempting to acquire lock '$lock'" }
    val lockAcquired = lockOperations.setIfAbsent(lock, "true") ?: false
    if (lockAcquired) {
      log.info { "Acquired lock '$lock'" }
      weather = readFromCache(locationId)
      try {
        if (weather == null) {
          weather = openWeatherMapClient.getCurrentWeatherGroup(listOf(locationId)).list[0]
          saveInCache(weather)
        }
      } finally {
        log.info { "Releasing lock '$lock'" }
        redisTemplate.delete(lock)
      }
    } else {
      // TODO: Wait for someone else to set the weather and return it
      log.info { "Could not acquire lock '$lock' another process must be fetching data" }
    }

    return weather
  }

  private fun now(): OffsetDateTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC)

  @Scheduled(initialDelay = 10000L, fixedDelay = 10000L)
  fun findRefreshableLocations() {
    log.debug { "scheduled: findRefreshableLocations()" }
    val before = now().minusMinutes(10L)
    while (true) {
      val redisKey = listOperations.leftPop(locationCachedRedisKey) as String? ?: break

      val locationRefresh = LocationRefresh.of(redisKey)
      if (locationRefresh.lastRefresh.isBefore(before)) {
        log.debug { "findRefreshableLocations: $redisKey scheduled for refresh" }
        listOperations.rightPush(locationForRefreshRedisKey, locationRefresh.locationId)
      } else {
        log.debug { "findRefreshableLocations: $redisKey is not eligible for refresh, pushing back" }
        listOperations.leftPush(locationCachedRedisKey, redisKey)
        break
      }
    }
  }

  @Scheduled(initialDelay = 15000L, fixedDelay = 60000L)
  fun refreshLocations() {
    log.debug { "scheduled: refreshLocations()" }
    val list = mutableListOf<Int>()
    var locationId = listOperations.leftPop(locationForRefreshRedisKey) as Int?
    while (locationId != null && list.size < 10) {
      list.add(locationId)
      locationId = listOperations.leftPop(locationForRefreshRedisKey) as Int?
    }

    if (list.isNotEmpty()) {
      log.info { "refreshLocations: Refreshing $list" }
      val weather = openWeatherMapClient.getCurrentWeatherGroup(list)
      log.debug { "refreshLocations: Got $weather" }
      weather.list.forEach { saveInCache(it) }
    }
  }

  private fun saveInCache(weather: CurrentWeather) {
    val locationRefresh = LocationRefresh(weather.cityId, now())
    log.info { "Saving weather for ${weather.cityId} to cache, ${locationRefresh.redisVal}" }
    valueOperations[weather.cityId] = weather.toJson()
    redisTemplate.expire(weather.cityId, Duration.ofMinutes(15))
    listOperations.rightPush(locationCachedRedisKey, locationRefresh.redisVal)
  }

  private fun readFromCache(locationId: Int): CurrentWeather? =
    (valueOperations[locationId] as String?)?.toCurrentWeather()

  private fun CurrentWeather.toJson() = objectMapper.writeValueAsString(this)
  private fun String.toCurrentWeather() = objectMapper.readValue(this, CurrentWeather::class.java)
}
