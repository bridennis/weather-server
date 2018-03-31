package weather.server.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import weather.server.component.Messaging;
import weather.server.entity.ResponseMessage;
import weather.server.entity.Weather;
import weather.server.util.Cache;

import static weather.server.config.RestApplicationConfig.*;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    @Autowired
    private Messaging messagingService;

    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance instance;

    /**
     * Сохраняем все клиентские запросы в очередь запросов.
     * Перед тем, как попасть в очередь, проверяем наличие результата в кэше.
     * Если клиентский запрос закэширован, сразу же возвращаем результат.
     * @param request Строка запроса
     */
    public void handleRequest(String request) {

        if (Cache.containsByQuery(request)) {
            Weather weather = Cache.getByQuery(request);

            log.info("Result from cache: {}", weather);

            // Возвращаем результат из кэша

            messagingService.sendMessage("/weather/" + request,  new ResponseMessage(200, weather));

            return;
        }

        IQueue<String> requestQueue = instance.getQueue(RQ_QUEUE_NAME);

        if (!requestQueue.contains(request)) {
            requestQueue.add(request);
        }
    }
}
