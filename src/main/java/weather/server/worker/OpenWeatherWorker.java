package weather.server.worker;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weather.server.entity.RequestType;
import weather.server.entity.ResponseMessage;
import weather.server.entity.Weather;
import weather.server.util.Request;

import java.util.concurrent.TimeUnit;

public class OpenWeatherWorker extends Worker {

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherWorker.class);

    private static final String APP_ID = "b0ec048eb79ef43898fa0633c060aee8";
    private static final String REQUEST_BY_NAME_FORMAT =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private static final String REQUEST_BY_GEO_FORMAT =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s";

    @Override
    public ResponseMessage getWeather(String request) {

//        try {
//            TimeUnit.SECONDS.sleep(10);
//        } catch (InterruptedException e) {}

        // Определяемся с типом полученного запроса и на основании этого формируем URL запроса

        RequestType requestType = Request.getType(request);

        log.info("{} got request: {} typed as {}", OpenWeatherWorker.class, request, requestType);

        String url;

        if (requestType.equals(RequestType.GEO)) {
            double[] latLon = Request.getLatLon(request);
            url = String.format(REQUEST_BY_GEO_FORMAT, latLon[0], latLon[1], APP_ID);
        } else {
            url = String.format(REQUEST_BY_NAME_FORMAT, request, APP_ID);
        }

        JsonObject jsonObject = getJsonObjectFromUrl(url);

        if (jsonObject.has("cod")) {

            if ("200".equals(jsonObject.get("cod").getAsString())) {

                Weather weather = new Weather();

                weather.setId(jsonObject.get("id").getAsInt());

                weather.setLat(jsonObject.getAsJsonObject("coord").get("lat").getAsDouble());
                weather.setLon(jsonObject.getAsJsonObject("coord").get("lon").getAsDouble());

                weather.setCity(jsonObject.get("name").getAsString());
                weather.setTemp(jsonObject.getAsJsonObject("main").get("temp").getAsInt());
                weather.setPressure(jsonObject.getAsJsonObject("main").get("pressure").getAsInt());
                weather.setHumidity(jsonObject.getAsJsonObject("main").get("humidity").getAsInt());
                weather.setWind(jsonObject.getAsJsonObject("wind").get("speed").getAsInt());
                weather.setClouds(jsonObject.getAsJsonObject("clouds").get("all").getAsInt());
                weather.setCountry(jsonObject.getAsJsonObject("sys").get("country").getAsString());

                return new ResponseMessage(200, weather);

            } else if ("503".equals(jsonObject.get("cod").getAsString())) {

                // Удаленный сервер недоступен
                log.warn("Requested URL [{}] unavailable", url);

                return new ResponseMessage(503, null);
            } else if ("404".equals(jsonObject.get("cod").getAsString())) {
                return new ResponseMessage(404, null);
            }
        }

        // При получении запроса с удаленного ресурса произошла общая ошибка I/O
        log.warn("Requested URL [{}] got an I/O error", url);

        return new ResponseMessage(520, null);
    }
}
