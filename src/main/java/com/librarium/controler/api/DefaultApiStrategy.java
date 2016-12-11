package com.librarium.controler.api;

import com.librarium.search.Elasticsearch;
import com.librarium.search.Namespace;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;

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

    public void search(JSONObject search, OutputStream outputStream, List<Namespace> namespacesList) {
        elasticsearch.search(search,outputStream,namespacesList);
    }
}
