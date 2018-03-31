package weather.server.entity;

import java.io.Serializable;

public class Weather implements Serializable {

    private int id;

    private Double lat;

    private Double lon;

    private String city;

    private String country;

    // Температура (градус Цельсия)
    private int temp;

    // Давление (мм ртутного столба)
    private int pressure;

    // Влажность (%)
    private int humidity;

    // Ветер (м/c)
    private int wind;

    // Облачность (%)
    private int clouds;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", wind=" + wind +
                ", clouds=" + clouds +
                '}';
    }
}
