package weather.server.worker;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.jayway.jsonpath.JsonPath;
import weather.server.entity.RequestType;
import weather.server.entity.GeoPoint;
import weather.server.entity.Weather;
import weather.server.util.Request;

import java.util.concurrent.TimeUnit;

import static weather.server.Main.*;

public class OpenWeatherWorker extends Worker implements Runnable {

    private final String request;

    private static final String REQUEST_BY_NAME_FORMAT = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    // http://api.openweathermap.org/data/2.5/weather?lat=59.91&lon=30.25&units=metric&appid=b0ec048eb79ef43898fa0633c060aee8
    private static final String REQUEST_BY_GEO_FORMAT = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s";

    private static final String APP_ID = "b0ec048eb79ef43898fa0633c060aee8";
    private static final long CACHE_TTL = 10 * 60;

    public OpenWeatherWorker(String request) {
        this.request = request;
    }

    public void run() {

        System.out.println(OpenWeatherWorker.class + " getting request: " + request);

        // Определяемся с типом полученного запроса и на основании этого формируем URL запроса

        RequestType requestType = Request.getType(request);

        String url;

        if (requestType.equals(RequestType.GEO)) {
            String[] latLon = request.split(" ");
            url = String.format(REQUEST_BY_GEO_FORMAT, latLon[0], latLon[1], APP_ID);
        } else {
            url = String.format(REQUEST_BY_NAME_FORMAT, request, APP_ID);
        }

        String response = getJsonFromUrl(url);

        if (!response.equals("")) {

            Weather weather = new Weather();

            int cityId = JsonPath.read(response, "$.id");

            Double lat = JsonPath.read(response, "$.coord.lat");
            Double lon = JsonPath.read(response, "$.coord.lon");

            weather.setCity((String) JsonPath.read(response, "$.name"));
            weather.setTemp((Integer) JsonPath.read(response, "$.main.temp"));
            weather.setPressure((Integer) JsonPath.read(response, "$.main.pressure"));
            weather.setHumidity((Integer) JsonPath.read(response, "$.main.humidity"));
            weather.setWind((Integer) JsonPath.read(response, "$.wind.speed"));
            weather.setClouds((Integer) JsonPath.read(response, "$.clouds.all"));
            weather.setCountry((String) JsonPath.read(response, "$.sys.country"));

            HazelcastInstance client = HazelcastClient.newHazelcastClient(new ClientConfig());

            /*
                Храним мапы:
                - основная: ID => Weather
                - связующие (запрос => ID):
                    - запрос по GEO координатам lat,lon
                    - запрос по населенному пункту: City
                    - запрос по полному GEO месту: City,Country
            */

            client.getMap(RS_MAP_BY_ID).put(
                    cityId,
                    weather,
                    CACHE_TTL, TimeUnit.SECONDS
            );

            // Раскладываем ID по связующим мапам
            client.getMap(RQ_MAP_CITY_COUNTRY_NAME).put(
                    String.format("%s,%s", weather.getCity().toLowerCase(), weather.getCountry().toLowerCase()),
                    cityId,
                    CACHE_TTL, TimeUnit.SECONDS
            );
            client.getMap(RQ_MAP_GEO_NAME).put(
                    new GeoPoint(lat, lon),
                    cityId,
                    CACHE_TTL, TimeUnit.SECONDS
            );
            client.getMap(RQ_MAP_CITY_NAME).put(
                    weather.getCity().toLowerCase(),
                    cityId,
                    CACHE_TTL, TimeUnit.SECONDS
            );

            // Удаляем запрос из очереди
            client.getQueue(RQ_QUEUE_NAME).remove(request);

            // Оповещаем о том, что запрос обработан

        }
    }
}
