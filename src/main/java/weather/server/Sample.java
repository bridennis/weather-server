package weather.server;

import com.hazelcast.core.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sample implements MessageListener {

    public static void main( String[] args ) {
        System.out.println(Pattern.compile("^.+?,[\\w]{2}$").matcher("Sankt-peterburg,RU").matches());
        System.out.println(Pattern.compile("^.+?,[\\w]{2}$").matcher(",RU").matches());
    }

    public void onMessage(Message message) {
        System.out.println("Message received = " + message);
    }

    // ...

    private final Executor messageExecutor = Executors.newSingleThreadExecutor();
}