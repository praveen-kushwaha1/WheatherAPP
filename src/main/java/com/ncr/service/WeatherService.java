package com.ncr.service;



import com.ncr.entity.Weather;
import com.ncr.repo.WeatherRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    /*
     * @Cacheable
     *
     * First request:
     *     DB -> return data -> store in cache
     *
     * Next request:
     *     Cache -> return data
     *
     * DB is not called when data is already present in cache.
     */
    @Cacheable(value = "weather", key = "#city")
    public String getWeatherByCity(String city) {

        System.out.println("Fetching data from DB for city: " + city);

        Optional<Weather> weather =
                weatherRepository.findByCity(city);

        return weather
                .map(Weather::getForecast)
                .orElse("Weather data not available");
    }


    /*
     * @CachePut
     *
     * Method always executes.
     *
     * Database is updated and the returned value
     * is placed into the cache.
     */
    @CachePut(value = "weather", key = "#city")
    public String updateWeather(
            String city,
            String updatedWeather) {

        weatherRepository.findByCity(city)
                .ifPresent(weather -> {

                    weather.setForecast(updatedWeather);

                    weatherRepository.save(weather);
                });

        return updatedWeather;
    }


    /*
     * @CacheEvict
     *
     * Deletes the database record and removes
     * the corresponding city from cache.
     */
    @Transactional
    @CacheEvict(value = "weather", key = "#city")
    public void deleteWeather(String city) {

        System.out.println(
                "Removing weather data for city: " + city
        );

        weatherRepository.deleteByCity(city);
    }
}