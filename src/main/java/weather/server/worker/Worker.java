package weather.server.worker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import weather.server.entity.ResponseMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;

abstract class Worker {

    abstract public ResponseMessage getWeather(String request);

    static JsonObject getJsonObjectFromUrl(String url) {

        try (Reader reader = new InputStreamReader(new URL(url).openStream(), "UTF-8")) {

            return new Gson().fromJson(reader, JsonObject.class);

        } catch (UnknownHostException e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("cod", "503");
            return jsonObject;

        } catch (FileNotFoundException e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("cod", "404");
            return jsonObject;

        } catch (IOException e) {
            return new JsonObject();
        }
    }
}
