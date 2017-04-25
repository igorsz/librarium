package com.librarium.eventhandler.controler;

import com.librarium.eventhandler.kafka.KafkaEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Igor on 08.02.2017.
 */

@RestController
@RequestMapping("/management")
public class ManagementController {


    @Autowired
    KafkaEventHandler kafkaMessageConsumer;

    @GetMapping(value = "/_health", produces = "text/plain")
    public ResponseEntity<String> getHealthStatus(){
        return new ResponseEntity<String>("green", getJsonHttpHeader(), HttpStatus.OK);
    }

    private HttpHeaders getJsonHttpHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
