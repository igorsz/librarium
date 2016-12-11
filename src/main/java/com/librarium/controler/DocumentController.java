package com.librarium.controler;

import com.librarium.configuration.Configuration;
import com.librarium.controler.api.ApiDispatcher;
import com.librarium.search.Elasticsearch;
import com.librarium.search.Namespace;
import com.librarium.search.NamespaceNameException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) throws IOException {
        Response search = elasticsearch.search();
        String response = name + configuration.getElasticsearchConfiguration().getNodes() + " Response: " + EntityUtils.toString(search.getEntity());;
        return response;
    }

    @PostMapping(value = "/_search", produces = "application/json")
    public ResponseEntity<String> search(@RequestBody JSONObject search, HttpServletResponse response) {
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search,outputStream);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(outputStream.toString(), httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/{namespaces}/_search", produces = "application/json")
    public ResponseEntity<String> search(@PathVariable List<String> namespaces, @RequestBody JSONObject search, HttpServletResponse response) throws NamespaceNameException {
        List<Namespace> namespacesList = prepareNamespaceList(namespaces);
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search,outputStream, namespacesList);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(outputStream.toString(), httpHeaders, HttpStatus.OK);
    }

    private List<Namespace> prepareNamespaceList(List<String> namespaces) throws NamespaceNameException {
        List<Namespace> namespacesList = new ArrayList<Namespace>();
        for(String namespace : namespaces){
            namespacesList.add(new Namespace(namespace));
        }
        return  namespacesList;
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Namespace not in lower case only")  // 400
    @ExceptionHandler(NamespaceNameException.class)
    public void conflict() {
        // Nothing to do
    }

}
