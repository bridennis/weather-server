package weather.server.entity;

public class ResponseMessage {

    private int code;

    private Weather weather;

    public ResponseMessage() {
    }

    public ResponseMessage(int code, Weather weather) {
        this.code = code;
        this.weather = weather;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "code=" + code +
                ", weather=" + weather +
                '}';
    }
}
