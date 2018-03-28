package weather.server;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import weather.server.worker.OpenWeatherWorker;

import java.util.concurrent.Executor;

public class RequestQueueListener implements ItemListener<String> {

    private Executor executor;

    public RequestQueueListener(Executor executor) {
        this.executor = executor;
    }

    public void itemAdded(ItemEvent item) {
        String request = (String) item.getItem();
        executor.execute(new OpenWeatherWorker(request));
    }

    public void itemRemoved(ItemEvent item) {
        System.out.println("Request [" + item.getItem() + "] removed.");
    }
}


