# open-weather-cache

Some caching functionality for openweather map

Test assignment for a job-interview:

 * Max 10000 calls to openweathermap / day, but support many more clients


## Ignored requirements

 * Filter on temperatures on favorite locations
 * Conversion of units

Rationale: Both of these are really client responsibilities and should not be part of the API. The client would hold a list of favorite positions and should cache weather for these location to be able to always show to the user. Client would refresh these periodically.

Filter: Since the client already have data cached showing only those locations should be simple to do in the client, should not require backend calls.

Conversion: Backend should only work in standard units, conversion is a display problem and should be done in the client.

## Not solved

10000 calls pr. day is one call every ~7 minutes. OpenWeatherMap supports a bit more than 200000 location ids. It is possible to query current weather with up to 20 locations in the same call, 200000/20 = 10000, thus if the app becomes popular enough that fills up our limit.

Forecast can only be queried one location at a time, so 10000 is definately to small for any real-world app.

For a real scenario it would probably be an idea to use the bulk api (paid account).

Because of time-constraint have not solved forecast-functionality at all.

## Further thoughts

Looking in retrospective at this is looks like I started to implement a queue-system in redis, it would probably be better to use a real queing system, maybe Kafka.
