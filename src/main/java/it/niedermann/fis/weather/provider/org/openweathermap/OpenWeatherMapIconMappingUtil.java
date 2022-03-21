package it.niedermann.fis.weather.provider.org.openweathermap;

import it.niedermann.fis.main.model.WeatherIconDto;
import it.niedermann.fis.weather.provider.DayNightIcon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static it.niedermann.fis.main.model.WeatherIconDto.*;

public class OpenWeatherMapIconMappingUtil {
    private static final Map<String, DayNightIcon> ICON_MAPPING = Collections.unmodifiableMap(new HashMap<>() {{
        put("200", new DayNightIcon(DAY_RAIN_THUNDER, NIGHT_FULL_MOON_RAIN_THUNDER));
        put("201", new DayNightIcon(RAIN_THUNDER, RAIN_THUNDER));
        put("202", new DayNightIcon(RAIN_THUNDER, RAIN_THUNDER));
        put("210", new DayNightIcon(THUNDER, THUNDER));
        put("211", new DayNightIcon(THUNDER, THUNDER));
        put("212", new DayNightIcon(THUNDER, THUNDER));
        put("221", new DayNightIcon(THUNDER, THUNDER));
        put("230", new DayNightIcon(DAY_RAIN_THUNDER, NIGHT_FULL_MOON_RAIN_THUNDER));
        put("231", new DayNightIcon(RAIN_THUNDER, RAIN_THUNDER));
        put("232", new DayNightIcon(RAIN_THUNDER, RAIN_THUNDER));
        put("300", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("301", new DayNightIcon(RAIN, RAIN));
        put("302", new DayNightIcon(RAIN, RAIN));
        put("310", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("311", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("312", new DayNightIcon(RAIN, RAIN));
        put("313", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("314", new DayNightIcon(RAIN, RAIN));
        put("321", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("500", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("501", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("502", new DayNightIcon(RAIN, RAIN));
        put("503", new DayNightIcon(RAIN, RAIN));
        put("504", new DayNightIcon(RAIN, RAIN));
        put("511", new DayNightIcon(SLEET, SLEET));
        put("520", new DayNightIcon(DAY_RAIN, NIGHT_FULL_MOON_RAIN));
        put("521", new DayNightIcon(RAIN, RAIN));
        put("522", new DayNightIcon(RAIN, RAIN));
        put("531", new DayNightIcon(RAIN, RAIN));
        put("600", new DayNightIcon(DAY_SNOW, NIGHT_FULL_MOON_SNOW));
        put("601", new DayNightIcon(SNOW, SNOW));
        put("602", new DayNightIcon(SNOW, SNOW));
        put("611", new DayNightIcon(DAY_SLEET, NIGHT_FULL_MOON_SLEET));
        put("612", new DayNightIcon(DAY_SLEET, NIGHT_FULL_MOON_SLEET));
        put("613", new DayNightIcon(SLEET, SLEET));
        put("615", new DayNightIcon(DAY_SLEET, NIGHT_FULL_MOON_SLEET));
        put("616", new DayNightIcon(SLEET, SLEET));
        put("620", new DayNightIcon(DAY_SNOW, NIGHT_FULL_MOON_SNOW));
        put("621", new DayNightIcon(SNOW, SNOW));
        put("622", new DayNightIcon(SNOW, SNOW));
        put("701", new DayNightIcon(MIST, MIST));
        put("711", new DayNightIcon(MIST, MIST));
        put("721", new DayNightIcon(MIST, MIST));
        put("731", new DayNightIcon(MIST, MIST));
        put("741", new DayNightIcon(MIST, MIST));
        put("751", new DayNightIcon(MIST, MIST));
        put("761", new DayNightIcon(MIST, MIST));
        put("762", new DayNightIcon(MIST, MIST));
        put("771", new DayNightIcon(MIST, MIST));
        put("781", new DayNightIcon(TORNADO, TORNADO));
        put("800", new DayNightIcon(DAY_CLEAR, NIGHT_FULL_MOON_CLEAR));
        put("801", new DayNightIcon(DAY_PARTIAL_CLOUD, NIGHT_FULL_MOON_PARTIAL_CLOUD));
        put("802", new DayNightIcon(CLOUDY, CLOUDY));
        put("803", new DayNightIcon(ANGRY_CLOUDS, ANGRY_CLOUDS));
        put("804", new DayNightIcon(ANGRY_CLOUDS, ANGRY_CLOUDS));
    }});

    public static WeatherIconDto get(String key, boolean isDay) {
        return isDay ? ICON_MAPPING.get(key).day() : ICON_MAPPING.get(key).night();
    }
}
