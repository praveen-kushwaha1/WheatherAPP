package com.ncr.controller;



import com.ncr.entity.Weather;
import com.ncr.repo.WeatherRepository;
import com.ncr.service.CacheInspectionService;
import com.ncr.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherRepository weatherRepository;
    private final CacheInspectionService cacheInspectionService;

    public WeatherController(
            WeatherService weatherService,
            WeatherRepository weatherRepository,
            CacheInspectionService cacheInspectionService) {

        this.weatherService = weatherService;
        this.weatherRepository = weatherRepository;
        this.cacheInspectionService = cacheInspectionService;
    }


    /*
     * GET weather by city
     *
     * Example:
     * GET /weather?city=Delhi
     */
    @GetMapping
    public String getWeather(
            @RequestParam String city) {

        return weatherService.getWeatherByCity(city);
    }


    /*
     * Add weather data.
     *
     * Example:
     *
     * POST /weather
     *
     * {
     *     "city": "Delhi",
     *     "forecast": "Sunny"
     * }
     */
    @PostMapping
    public Weather addWeather(
            @RequestBody Weather weather) {

        return weatherRepository.save(weather);
    }


    /*
     * Get all weather records.
     *
     * Example:
     * GET /weather/all
     */
    @GetMapping("/all")
    public List<Weather> getAllWeather() {

        return weatherRepository.findAll();
    }


    /*
     * Display current cache contents in console.
     *
     * Example:
     * GET /weather/cacheData
     */
    @GetMapping("/cacheData")
    public String getCacheData() {

        cacheInspectionService.printCacheContents("weather");

        return "Cache contents printed in application console.";
    }


    /*
     * Update weather.
     *
     * Example:
     * PUT /weather/Delhi?weatherUpdate=Rainy
     */
    @PutMapping("/{city}")
    public String updateWeather(
            @PathVariable String city,
            @RequestParam String weatherUpdate) {

        return weatherService.updateWeather(
                city,
                weatherUpdate
        );
    }


    /*
     * Delete weather.
     *
     * Example:
     * DELETE /weather/Delhi
     */
    @DeleteMapping("/{city}")
    public String deleteWeather(
            @PathVariable String city) {

        weatherService.deleteWeather(city);

        return "Weather data for " + city
                + " has been deleted and cache evicted.";
    }
}