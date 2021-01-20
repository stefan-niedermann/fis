package it.niedermann.fis.weather.provider.openweathermap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpenWeatherMapIconMappingUtil {
    private static final Map<String, DayNightIcon> ICON_MAPPING = Collections.unmodifiableMap(new HashMap<>() {{
        put("200", new DayNightIcon("day_rain_thunder", "night_full_moon_rain_thunder"));
        put("201", new DayNightIcon("rain_thunder", "rain_thunder"));
        put("202", new DayNightIcon("rain_thunder", "rain_thunder"));
        put("210", new DayNightIcon("thunder", "thunder"));
        put("211", new DayNightIcon("thunder", "thunder"));
        put("212", new DayNightIcon("thunder", "thunder"));
        put("221", new DayNightIcon("thunder", "thunder"));
        put("230", new DayNightIcon("day_rain_thunder", "night_full_moon_rain_thunder"));
        put("231", new DayNightIcon("rain_thunder", "rain_thunder"));
        put("232", new DayNightIcon("rain_thunder", "rain_thunder"));
        put("300", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("301", new DayNightIcon("rain", "rain"));
        put("302", new DayNightIcon("rain", "rain"));
        put("310", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("311", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("312", new DayNightIcon("rain", "rain"));
        put("313", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("314", new DayNightIcon("rain", "rain"));
        put("321", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("500", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("501", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("502", new DayNightIcon("rain", "rain"));
        put("503", new DayNightIcon("rain", "rain"));
        put("504", new DayNightIcon("rain", "rain"));
        put("511", new DayNightIcon("sleet", "sleet"));
        put("520", new DayNightIcon("day_rain", "night_full_moon_rain"));
        put("521", new DayNightIcon("rain", "rain"));
        put("522", new DayNightIcon("rain", "rain"));
        put("531", new DayNightIcon("rain", "rain"));
        put("600", new DayNightIcon("day_snow", "night_full_moon_snow"));
        put("601", new DayNightIcon("snow", "snow"));
        put("602", new DayNightIcon("snow", "snow"));
        put("611", new DayNightIcon("day_sleet", "night_full_moon_sleet"));
        put("612", new DayNightIcon("day_sleet", "night_full_moon_sleet"));
        put("613", new DayNightIcon("sleet", "sleet"));
        put("615", new DayNightIcon("day_sleet", "night_full_moon_sleet"));
        put("616", new DayNightIcon("sleet", "sleet"));
        put("620", new DayNightIcon("day_snow", "night_full_moon_snow"));
        put("621", new DayNightIcon("snow", "snow"));
        put("622", new DayNightIcon("snow", "snow"));
        put("701", new DayNightIcon("mist", "mist"));
        put("711", new DayNightIcon("mist", "mist"));
        put("721", new DayNightIcon("mist", "mist"));
        put("731", new DayNightIcon("mist", "mist"));
        put("741", new DayNightIcon("mist", "mist"));
        put("751", new DayNightIcon("mist", "mist"));
        put("761", new DayNightIcon("mist", "mist"));
        put("762", new DayNightIcon("mist", "mist"));
        put("771", new DayNightIcon("mist", "mist"));
        put("781", new DayNightIcon("tornado", "tornado"));
        put("800", new DayNightIcon("day_clear", "night_full_moon_clear"));
        put("801", new DayNightIcon("day_partial_cloud", "night_full_moon_partial_cloud"));
        put("802", new DayNightIcon("cloudy", "cloudy"));
        put("803", new DayNightIcon("angry_clouds", "angry_clouds"));
        put("804", new DayNightIcon("angry_clouds", "angry_clouds"));
    }});

    public static String get(String key, boolean isDay) {
        return isDay ? ICON_MAPPING.get(key).day : ICON_MAPPING.get(key).night;
    }

    private static class DayNightIcon {
        private final String day;
        private final String night;

        public DayNightIcon(String day, String night) {
            this.day = day;
            this.night = night;
        }
    }
}
