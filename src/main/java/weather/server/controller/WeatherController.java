package weather.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import weather.server.entity.RequestMessage;
import weather.server.service.WeatherService;

@Controller
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @MessageMapping("/request")
    public void receiveRequest(RequestMessage request) {

        log.info("Get request {}", request.getRequest());

        weatherService.handleRequest(request.getRequest());
    }
}