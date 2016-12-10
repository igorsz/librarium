package com.librarium.controler.api;

import com.librarium.search.Elasticsearch;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * Created by Igor on 08.12.2016.
 */

@Component
public class DefaultApiStrategy implements ApiStrategy {

    @Autowired
    Elasticsearch elasticsearch;

    public void search(JSONObject search, OutputStream outputStream) {
        elasticsearch.search(search,outputStream);
    }
}
