package com.librarium.controler;

import com.librarium.configuration.Configuration;
import com.librarium.controler.api.ApiDispatcher;
import com.librarium.search.Elasticsearch;
import org.elasticsearch.action.search.SearchResponse;
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
 * Created by Igor on 29.11.2016.
 */

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    Configuration configuration;

    @Autowired
    Elasticsearch elasticsearch;

    @Autowired
    ApiDispatcher apiDispatcher;

    private static final String template = "Hello, %s!";

    @RequestMapping(value = "/greeting", produces = "application/json")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        SearchResponse search = elasticsearch.search();
        String response = name + configuration.getElasticsearchConfiguration().getNodes() + " Response: " + search.toString();
        return response;
    }

    @PostMapping(value = "/_search", produces = "application/json")
    public ResponseEntity<String> search(@RequestBody JSONObject search) {
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search,outputStream);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>("{\"test\": \"jsonResponseExample\"}", httpHeaders, HttpStatus.OK);
    }

}
