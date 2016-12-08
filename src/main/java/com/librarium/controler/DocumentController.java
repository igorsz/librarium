package com.librarium.controler;

import com.librarium.configuration.Configuration;
import com.librarium.search.Elasticsearch;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Igor on 29.11.2016.
 */

@RestController
public class DocumentController {

    @Autowired
    Configuration configuration;

    @Autowired
    Elasticsearch elasticsearch;

    private static final String template = "Hello, %s!";

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        SearchResponse search = elasticsearch.search();
        String response = name+configuration.getElasticsearchConfiguration().getNodes() + " Response: " + search.toString();
        return response;
    }

}
