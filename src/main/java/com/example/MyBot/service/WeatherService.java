package com.example.MyBot.service;

import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    @Value("${weather.api.key}")
    private String apiKey;

    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    public void execute(CommandContext context) {
        String city = context.getArgs().trim();

        if (city.isEmpty()) {
            context.getChannel().sendMessage(
                    "🌤️ Please provide a city! Usage: `!weather <city>`\n" +
                            "Example: `!weather London`"
            ).queue();
            return;
        }

        log.info("🌤️ Weather requested for city: {}", city);
        context.getChannel().sendTyping().queue();

        try {
            String weather = fetchWeather(city);
            context.getChannel().sendMessage(weather).queue(
                    ok  -> log.debug("✅ Weather sent"),
                    err -> log.error("❌ Failed to send weather", err)
            );
        } catch (Exception e) {
            log.error("Error fetching weather for: {}", city, e);
            context.getChannel().sendMessage(
                    "❌ Could not find weather for **" + city
                            + "**. Check the city name and try again!"
            ).queue();
        }
    }

    private String fetchWeather(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = String.format(WEATHER_URL, encodedCity, apiKey);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 404) {
            return "❌ City **" + city + "** not found!";
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("Weather API error: " + response.statusCode());
        }

        return parseWeather(city, response.body());
    }

    private String parseWeather(String city, String json) {
        try {
            // Extract temperature
            String temp = extractValue(json, "\"temp\":");
            String feelsLike = extractValue(json, "\"feels_like\":");
            String humidity = extractValue(json, "\"humidity\":");
            String windSpeed = extractValue(json, "\"speed\":");

            // Extract description
            String description = "";
            int descIdx = json.indexOf("\"description\":\"");
            if (descIdx != -1) {
                int start   = descIdx + 15;
                int end     = json.indexOf("\"", start);
                description = json.substring(start, end);
            }

            // Extract city name from response
            String cityName = city;
            int nameIdx = json.indexOf("\"name\":\"");
            if (nameIdx != -1) {
                int start = nameIdx + 8;
                int end   = json.indexOf("\"", start);
                cityName  = json.substring(start, end);
            }

            // Pick emoji based on description
            String emoji = getWeatherEmoji(description);

            return String.format(
                    "%s **Weather in %s**\n\n" +
                            "🌡️ **Temp:** %s°C (Feels like %s°C)\n" +
                            "💧 **Humidity:** %s%%\n" +
                            "💨 **Wind:** %s m/s\n" +
                            "☁️ **Condition:** %s",
                    emoji, cityName, temp, feelsLike,
                    humidity, windSpeed, description
            );

        } catch (Exception e) {
            log.error("Failed to parse weather", e);
            return "❌ Could not read weather data!";
        }
    }

    private String extractValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return "N/A";
        int start = idx + key.length();
        int end   = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).trim();
    }

    private String getWeatherEmoji(String description) {
        if (description.contains("clear"))  return "☀️";
        if (description.contains("cloud"))  return "☁️";
        if (description.contains("rain"))   return "🌧️";
        if (description.contains("storm"))  return "⛈️";
        if (description.contains("snow"))   return "❄️";
        if (description.contains("mist") ||
                description.contains("fog"))    return "🌫️";
        return "🌤️";
    }
}