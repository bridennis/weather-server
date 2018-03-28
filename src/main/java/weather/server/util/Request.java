package weather.server.util;

import weather.server.entity.RequestType;

import java.util.regex.Pattern;

/**
 * Класс "утилитных" методов для сущности Запрос.
 */
public class Request {

    /**
     * Возвращает тип запроса.
     *
     * @param request Строковый запрос
     * @return RequestType
     */
    public static RequestType getType(String request) {

        RequestType requestType = RequestType.CITY;

        if (Pattern.compile("^.+?,[\\w]{2}$").matcher(request).matches()) {
            requestType = RequestType.CITY_COUNTRY;
        } else if (Pattern.compile("^[0-9\\.]+ [0-9\\.]+$").matcher(request).matches()){
            requestType = RequestType.GEO;
        }

        return requestType;
    }

    public static double[] getLatLon(String request) {
        double[] latLon = new double[2];

        if (Pattern.compile("^[0-9\\.]+ [0-9\\.]+$").matcher(request).matches()) {
            String[] coords = request.split(" ");
            latLon[0] = Double.parseDouble(coords[0]);
            latLon[1] = Double.parseDouble(coords[1]);
        }

        return latLon;
    }
}
