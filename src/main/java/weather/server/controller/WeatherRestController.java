package weather.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import weather.server.entity.Weather;
import weather.server.repository.WeatherRepository;

@RestController
@RequestMapping("/weather")
public class WeatherRestController {

    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherRestController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{request}")
    public Weather get() {
        return new Weather();
    }
}
