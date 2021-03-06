package it.niedermann.fis.operation;

import java.util.Arrays;
import java.util.Objects;

public class OperationDto {

    public String keyword = "";
    public String[] tags = new String[0];
    public String[] vehicles = new String[0];
    public String note = "";

    public String street = "";
    public String number = "";
    public String location = "";
    public String obj = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationDto that = (OperationDto) o;
        return Objects.equals(keyword, that.keyword) && Arrays.equals(tags, that.tags) && Arrays.equals(vehicles, that.vehicles) && Objects.equals(note, that.note) && Objects.equals(street, that.street) && Objects.equals(number, that.number) && Objects.equals(location, that.location) && Objects.equals(obj, that.obj);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(keyword, note, street, number, location, obj);
        result = 31 * result + Arrays.hashCode(tags);
        result = 31 * result + Arrays.hashCode(vehicles);
        return result;
    }

    @Override
    public String toString() {
        return "OperationDto{" +
                "keyword='" + keyword + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", vehicles=" + Arrays.toString(vehicles) +
                ", note='" + note + '\'' +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", location='" + location + '\'' +
                ", object='" + obj + '\'' +
                '}';
    }
}
