package it.niedermann.fis.weather;

import java.util.Objects;

public class WeatherDto {
    public float temperature;
    public String icon;
    public boolean isDay;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherDto that = (WeatherDto) o;
        return Float.compare(that.temperature, temperature) == 0 && isDay == that.isDay && Objects.equals(icon, that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, icon, isDay);
    }

    @Override
    public String toString() {
        return "WeatherDto{" +
                "temperature=" + temperature +
                ", icon='" + icon + '\'' +
                ", isDay=" + isDay +
                '}';
    }
}
