package weather.server.util;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import weather.server.entity.GeoPoint;
import weather.server.entity.Weather;

import static weather.server.Main.*;

/**
 * Класс "утилитных" методов для работы с Кэшем.
 */
public class Cache {

    private static final HazelcastInstance client = HazelcastClient.newHazelcastClient(new ClientConfig());

    public static boolean containsByQuery(String key) {

        switch (Request.getType(key)) {
            case CITY_COUNTRY:
                return client.getMap(RQ_MAP_CITY_COUNTRY_NAME).containsKey(key.toLowerCase());
            case GEO:
                double[] latLon = Request.getLatLon(key);
                return client.getMap(RQ_MAP_GEO_NAME).containsKey(new GeoPoint(latLon[0], latLon[1]));
            case CITY:
                return client.getMap(RQ_MAP_CITY_NAME).containsKey(key.toLowerCase());
        }

        return false;
    }

    public static Weather getByQuery(String key) {

        Integer id = null;

        switch (Request.getType(key)) {
            case CITY_COUNTRY:
                id = (Integer) client.getMap(RQ_MAP_CITY_COUNTRY_NAME).get(key.toLowerCase());
                break;
            case GEO:
                double[] latLon = Request.getLatLon(key);
                id = (Integer) client.getMap(RQ_MAP_GEO_NAME).get(new GeoPoint(latLon[0], latLon[1]));
                break;
            case CITY:
                id = (Integer) client.getMap(RQ_MAP_CITY_NAME).get(key.toLowerCase());
                break;
        }

        return id != null ? (Weather) client.getMap(RS_MAP_BY_ID).get(id): null;
    }
}
