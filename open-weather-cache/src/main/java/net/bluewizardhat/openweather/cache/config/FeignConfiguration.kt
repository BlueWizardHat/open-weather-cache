package net.bluewizardhat.openweather.cache.config

import feign.Client
import feign.okhttp.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfiguration {
  @Bean
  fun client(): Client {
    return OkHttpClient()
  }
}
