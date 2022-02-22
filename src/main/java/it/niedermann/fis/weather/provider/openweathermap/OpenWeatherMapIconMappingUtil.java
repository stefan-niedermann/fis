package it.niedermann.fis.weather.provider.openweathermap;

import it.niedermann.fis.main.model.WeatherIconDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpenWeatherMapIconMappingUtil {
    private static final Map<String, DayNightIcon> ICON_MAPPING = Collections.unmodifiableMap(new HashMap<>() {{
        put("200", new DayNightIcon(WeatherIconDto.DAY_RAIN_THUNDER, WeatherIconDto.NIGHT_FULL_MOON_RAIN_THUNDER));
        put("201", new DayNightIcon(WeatherIconDto.RAIN_THUNDER, WeatherIconDto.RAIN_THUNDER));
        put("202", new DayNightIcon(WeatherIconDto.RAIN_THUNDER, WeatherIconDto.RAIN_THUNDER));
        put("210", new DayNightIcon(WeatherIconDto.THUNDER, WeatherIconDto.THUNDER));
        put("211", new DayNightIcon(WeatherIconDto.THUNDER, WeatherIconDto.THUNDER));
        put("212", new DayNightIcon(WeatherIconDto.THUNDER, WeatherIconDto.THUNDER));
        put("221", new DayNightIcon(WeatherIconDto.THUNDER, WeatherIconDto.THUNDER));
        put("230", new DayNightIcon(WeatherIconDto.DAY_RAIN_THUNDER, WeatherIconDto.NIGHT_FULL_MOON_RAIN_THUNDER));
        put("231", new DayNightIcon(WeatherIconDto.RAIN_THUNDER, WeatherIconDto.RAIN_THUNDER));
        put("232", new DayNightIcon(WeatherIconDto.RAIN_THUNDER, WeatherIconDto.RAIN_THUNDER));
        put("300", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("301", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("302", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("310", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("311", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("312", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("313", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("314", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("321", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("500", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("501", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("502", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("503", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("504", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("511", new DayNightIcon(WeatherIconDto.SLEET, WeatherIconDto.SLEET));
        put("520", new DayNightIcon(WeatherIconDto.DAY_RAIN, WeatherIconDto.NIGHT_FULL_MOON_RAIN));
        put("521", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("522", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("531", new DayNightIcon(WeatherIconDto.RAIN, WeatherIconDto.RAIN));
        put("600", new DayNightIcon(WeatherIconDto.DAY_SNOW, WeatherIconDto.NIGHT_FULL_MOON_SNOW));
        put("601", new DayNightIcon(WeatherIconDto.SNOW, WeatherIconDto.SNOW));
        put("602", new DayNightIcon(WeatherIconDto.SNOW, WeatherIconDto.SNOW));
        put("611", new DayNightIcon(WeatherIconDto.DAY_SLEET, WeatherIconDto.NIGHT_FULL_MOON_SLEET));
        put("612", new DayNightIcon(WeatherIconDto.DAY_SLEET, WeatherIconDto.NIGHT_FULL_MOON_SLEET));
        put("613", new DayNightIcon(WeatherIconDto.SLEET, WeatherIconDto.SLEET));
        put("615", new DayNightIcon(WeatherIconDto.DAY_SLEET, WeatherIconDto.NIGHT_FULL_MOON_SLEET));
        put("616", new DayNightIcon(WeatherIconDto.SLEET, WeatherIconDto.SLEET));
        put("620", new DayNightIcon(WeatherIconDto.DAY_SNOW, WeatherIconDto.NIGHT_FULL_MOON_SNOW));
        put("621", new DayNightIcon(WeatherIconDto.SNOW, WeatherIconDto.SNOW));
        put("622", new DayNightIcon(WeatherIconDto.SNOW, WeatherIconDto.SNOW));
        put("701", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("711", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("721", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("731", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("741", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("751", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("761", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("762", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("771", new DayNightIcon(WeatherIconDto.MIST, WeatherIconDto.MIST));
        put("781", new DayNightIcon(WeatherIconDto.TORNADO, WeatherIconDto.TORNADO));
        put("800", new DayNightIcon(WeatherIconDto.DAY_CLEAR, WeatherIconDto.NIGHT_FULL_MOON_CLEAR));
        put("801", new DayNightIcon(WeatherIconDto.DAY_PARTIAL_CLOUD, WeatherIconDto.NIGHT_FULL_MOON_PARTIAL_CLOUD));
        put("802", new DayNightIcon(WeatherIconDto.CLOUDY, WeatherIconDto.CLOUDY));
        put("803", new DayNightIcon(WeatherIconDto.ANGRY_CLOUDS, WeatherIconDto.ANGRY_CLOUDS));
        put("804", new DayNightIcon(WeatherIconDto.ANGRY_CLOUDS, WeatherIconDto.ANGRY_CLOUDS));
    }});

    public static WeatherIconDto get(String key, boolean isDay) {
        return isDay ? ICON_MAPPING.get(key).day : ICON_MAPPING.get(key).night;
    }

    private static class DayNightIcon {
        private final WeatherIconDto day;
        private final WeatherIconDto night;

        public DayNightIcon(WeatherIconDto day, WeatherIconDto night) {
            this.day = day;
            this.night = night;
        }
    }
}
