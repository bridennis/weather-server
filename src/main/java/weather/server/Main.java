package weather.server;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import weather.server.entity.Weather;
import weather.server.util.Cache;

import java.util.concurrent.Executors;

public class Main {

    private static final int EXECUTOR_NUM_THREADS = 5;      // Максимальное количество одновременных потоков (запросов) к API ресурсу
    private static final int DELAY_BETWEEN_QUERY = 1000;    // Задержка между запросами к API ресурсу (ms)

    public static final String RQ_QUEUE_NAME = "rqQueue";   // Наименование очереди запросов с клиентской части

    public static final String RS_MAP_BY_ID = "rsByCityId";    // Наименование кэш-мапы ответов

    // Наименование связующей мапы: запрос (city,country) -> id ответа
    public static final String RQ_MAP_CITY_COUNTRY_NAME = "rqByCityCountry";

    // Наименование связующей мапы: запрос (geo координаты) -> id ответа
    public static final String RQ_MAP_GEO_NAME = "rqByGeoPoint";

    // Наименование связующей мапы: запрос (city) -> id ответа
    public static final String RQ_MAP_CITY_NAME = "rqByCityName";

    public static void main(String[] args) {

        HazelcastInstance server = Hazelcast.newHazelcastInstance(new Config());

        IQueue<String> requestQueue = server.getQueue(RQ_QUEUE_NAME);

        requestQueue.addItemListener(
                new RequestQueueListener(Executors.newFixedThreadPool(EXECUTOR_NUM_THREADS)),
                true
        );

        String clientQuery = "Sankt-Peterburg";
        clientQuery = "Sankt-Peterburg,ru";
        clientQuery = "59.92 30.25";

        if (Cache.containsByQuery(clientQuery)) {
            Weather weather = Cache.getByQuery(clientQuery);

            System.out.println("Результат из кэша:");
            System.out.println(weather);
            return; // Возвращаем результат из кэша
        }

        if (requestQueue.contains(clientQuery)) {
            // Запрос уже есть в очереди на обработку
            System.out.println("Аналогичный запрос уже есть в очереди!");
            return; // Подписываемся на его результат
        } else {
            requestQueue.add(clientQuery);
        }
        System.out.println("requestQueue size: " + requestQueue.size());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(server.getMap(RQ_MAP_CITY_NAME).size());
        System.out.println(server.getMap(RQ_MAP_GEO_NAME).size());
        System.out.println(server.getMap(RQ_MAP_CITY_COUNTRY_NAME).size());

        System.out.println(Cache.getByQuery(clientQuery));

        System.out.println();
    }
}
