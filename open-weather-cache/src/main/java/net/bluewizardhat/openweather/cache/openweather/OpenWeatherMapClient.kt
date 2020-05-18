package net.bluewizardhat.openweather.cache.openweather

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Client
import feign.Feign
import feign.Headers
import feign.Param
import feign.Request
import feign.RequestLine
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import mu.KotlinLogging
import net.bluewizardhat.openweather.cache.openweather.model.CurrentWeatherGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.validation.constraints.Size

@Component
class OpenWeatherMapClient @Autowired constructor(
  @Value("\${openweathermap.api.baseurl}")
  private val endpoint: String,
  @Value("\${openweathermap.api.appid}")
  private val appid: String,
  objectMapper: ObjectMapper,
  client: Client
) {
  private val log = KotlinLogging.logger {}

  interface FeignApi {
    companion object {
      const val urlPrefix = "/data/2.5"
    }

    @RequestLine("GET $urlPrefix/group?id={locationIds}&appid={appId}")
    @Headers("Content-Type: application/json")
    fun getCurrentWeatherGroup(@Param("locationIds") locationIds: String, @Param("appId") appid: String): CurrentWeatherGroup

    @RequestLine("GET $urlPrefix/forecast?id={locationId}&appid={appId}")
    @Headers("Content-Type: application/json")
    fun getForecast(@Param("locationId") locationId: Int, @Param("appId") appid: String): Map<String, Any>
  }

  private val jsonClient = Feign.builder()
    .client(client)
    .encoder(JacksonEncoder(objectMapper))
    .decoder(JacksonDecoder(objectMapper))
    .retryer(Retryer.NEVER_RETRY)
    .options(Request.Options(10L, TimeUnit.SECONDS, 60L, TimeUnit.SECONDS, true))
    // .errorDecoder()
    .target(FeignApi::class.java, endpoint)

  fun getCurrentWeatherGroup(@Size(max = 20) locationIds: List<Int>): CurrentWeatherGroup {
    log.info { "Fetching weather from $endpoint for $locationIds" }
    return jsonClient.getCurrentWeatherGroup(locationIds.joinToString(separator = ","), appid)
  }

  fun getForecast(locationId: Int): Map<String, Any> {
    log.info { "Fetching forecast from $endpoint for $locationId" }
    return jsonClient.getForecast(locationId, appid)
  }
}
