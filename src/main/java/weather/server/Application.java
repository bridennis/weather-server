package weather.server;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import weather.server.component.ApplicationContextProvider;
import weather.server.entity.GeoPoint;
import weather.server.entity.ResponseMessage;
import weather.server.component.Messaging;
import weather.server.worker.OpenWeatherWorker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static weather.server.config.RestApplicationConfig.*;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        HazelcastInstance instance = ApplicationContextProvider.getApplicationContext()
                .getBean(HazelcastInstance.class);
        Messaging messaging = ApplicationContextProvider.getApplicationContext().getBean(Messaging.class);

        IQueue rqQueue = instance.getQueue(RQ_QUEUE_NAME);

        Runnable task = () -> {

            if (rqQueue.size() > 0) {
                String request = (String) rqQueue.peek();
                String destination = "/weather/" + request;

                OpenWeatherWorker openWeatherWorker = new OpenWeatherWorker();
                ResponseMessage rsMessage = openWeatherWorker.getWeather(request);

                switch (rsMessage.getCode()) {
                    case 200:

                        // Сохраняем результат в кэше
                        instance.getMap(RS_MAP_BY_ID).put(
                                rsMessage.getWeather().getId(),
                                rsMessage.getWeather(),
                                MAP_TTL_SEC, TimeUnit.SECONDS
                        );

                        // Раскладываем ID по связующим мапам
                        instance.getMap(RQ_MAP_CITY_COUNTRY_NAME).put(
                                String.format("%s,%s",
                                        rsMessage.getWeather().getCity().toLowerCase(),
                                        rsMessage.getWeather().getCountry().toLowerCase()),
                                rsMessage.getWeather().getId(),
                                MAP_TTL_SEC, TimeUnit.SECONDS
                        );
                        instance.getMap(RQ_MAP_GEO_NAME).put(
                                new GeoPoint(rsMessage.getWeather().getLat(), rsMessage.getWeather().getLon()),
                                rsMessage.getWeather().getId(),
                                MAP_TTL_SEC, TimeUnit.SECONDS
                        );
                        instance.getMap(RQ_MAP_CITY_NAME).put(
                                rsMessage.getWeather().getCity().toLowerCase(),
                                rsMessage.getWeather().getId(),
                                MAP_TTL_SEC, TimeUnit.SECONDS
                        );


                        messaging.sendMessage(destination, rsMessage);
                        break;

                    case 404:
                    case 503:
                    case 520:
                        messaging.sendMessage(destination, rsMessage);
                        break;
                }

                rqQueue.remove(request);
            }
        };

        executor.scheduleAtFixedRate(task, 0, DELAY_BETWEEN_QUERY, TimeUnit.MILLISECONDS);
    }
}
