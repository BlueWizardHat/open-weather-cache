package net.bluewizardhat.openweather.cache.redis

import java.time.OffsetDateTime

data class LocationRefresh(
  val locationId: Int,
  val lastRefresh: OffsetDateTime
) {
  companion object {
    fun of(redisVal: String): LocationRefresh {
      val split = redisVal.split("|")
      val locationId = split[0].toInt()
      val lastRefresh = OffsetDateTime.parse(split[1])
      return LocationRefresh(locationId, lastRefresh)
    }
  }

  val redisVal = "$locationId|$lastRefresh"
}
