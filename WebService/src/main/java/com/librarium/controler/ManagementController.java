package com.librarium.controler;

import com.librarium.controler.api.ApiDispatcher;
import com.librarium.kafka.KafkaMessageProducer;
import com.librarium.event.Index;
import com.librarium.event.exceptions.IndexNameException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Igor on 11.12.2016.
 */

@RestController
@RequestMapping("/management")
public class ManagementController {

    @Autowired
    ApiDispatcher dispatcher;

    @Autowired
    KafkaMessageProducer kafkaMessageProducer;

    @PutMapping(value = "/{index}", produces = "application/json")
    public ResponseEntity<String> createIndex(@PathVariable String index,
                                              @RequestBody JSONObject body) throws IndexNameException {
        OutputStream outputStream = new ByteArrayOutputStream();
        dispatcher.createIndex(new Index(index),body,outputStream);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{index}", produces = "application/json")
    public ResponseEntity<String> deleteIndex(@PathVariable String index) throws IndexNameException {
        OutputStream outputStream = new ByteArrayOutputStream();
        dispatcher.deleteIndex(new Index(index),outputStream);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @GetMapping(value = "/_indices", produces = "text/plain")
    public ResponseEntity<String> listIndices(){
        OutputStream outputStream = new ByteArrayOutputStream();
        dispatcher.listIndices(outputStream);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }


    private HttpHeaders getJsonHttpHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
