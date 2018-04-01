package weather.server.util;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Component;
import weather.server.component.ApplicationContextProvider;
import weather.server.entity.GeoPoint;
import weather.server.entity.Weather;

import static weather.server.config.RestApplicationConfig.*;

/**
 * Класс "утилитных" методов для работы с Кэшем.
 */

@Component
public class Cache {

    private static HazelcastInstance instance = ApplicationContextProvider.getApplicationContext().getBean(HazelcastInstance.class);

    public static boolean contains(String key) {

        switch (Request.getType(key)) {
            case CITY_COUNTRY:
                return instance.getMap(RQ_MAP_CITY_COUNTRY_NAME).containsKey(key.toLowerCase());
            case GEO:
                double[] latLon = Request.getLatLon(key);
                return instance.getMap(RQ_MAP_GEO_NAME).containsKey(new GeoPoint(latLon[0], latLon[1]));
            case CITY:
                return instance.getMap(RQ_MAP_CITY_NAME).containsKey(key.toLowerCase());
        }

        return false;
    }

    public static Weather getByQuery(String key) {

        Integer id = null;

        switch (Request.getType(key)) {
            case CITY_COUNTRY:
                id = (Integer) instance.getMap(RQ_MAP_CITY_COUNTRY_NAME).get(key.toLowerCase());
                break;
            case GEO:
                double[] latLon = Request.getLatLon(key);
                id = (Integer) instance.getMap(RQ_MAP_GEO_NAME).get(new GeoPoint(latLon[0], latLon[1]));
                break;
            case CITY:
                id = (Integer) instance.getMap(RQ_MAP_CITY_NAME).get(key.toLowerCase());
                break;
        }

        return id != null ? (Weather) instance.getMap(RS_MAP_BY_ID).get(id): null;
    }
}
