package weather.server.worker;

import org.jsoup.Jsoup;

import java.io.IOException;

abstract class Worker {

    public static String getJsonFromUrl(String url) {
        try {
            return Jsoup.connect(url).ignoreContentType(true).execute().body();
        } catch (IOException e) {
            return  "";
        }
    }
}
