package com.librarium.controler;

import com.librarium.configuration.Configuration;
import com.librarium.controler.api.ApiDispatcher;
import com.librarium.search.Elasticsearch;
import com.librarium.search.Index;
import com.librarium.search.IndexNameException;
import com.librarium.search.Type;
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
    ApiDispatcher apiDispatcher;

    private static final String template = "Hello, %s!";

    @PostMapping(value = "/_search", produces = "application/json")
    public ResponseEntity<String> search(@RequestBody JSONObject search, HttpServletResponse response) {
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search,outputStream);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @PostMapping(value = "/{indices}/_search", produces = "application/json")
    public ResponseEntity<String> search(@PathVariable List<String> indices,
                                         @RequestBody JSONObject search) throws IndexNameException {
        List<Index> indexList = prepareIndicesList(indices);
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search,outputStream, indexList);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @PostMapping(value = "/{indices}/{types}/_search", produces = "application/json")
    public ResponseEntity<String> search(@PathVariable List<String> indices,
                                         @PathVariable List<String> types,
                                         @RequestBody JSONObject search) throws IndexNameException {
        List<Index> indexList = prepareIndicesList(indices);
        List<Type> typeList = prepareTypesList(types);
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search,outputStream, indexList, typeList);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    private List<Type> prepareTypesList(List<String> types) {
        List<Type> typeList = new ArrayList<Type>();
        for(String type : types) typeList.add(new Type(type));
        return typeList;
    }

    private List<Index> prepareIndicesList(List<String> indices) throws IndexNameException {
        List<Index> indicesList = new ArrayList<Index>();
        for(String namespace : indices) indicesList.add(new Index(namespace));
        return  indicesList;
    }

    private HttpHeaders getJsonHttpHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    //Exception mappings
    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Index not in lower case only")  // 400
    @ExceptionHandler(IndexNameException.class)
    public void conflict() {
        // Nothing to do
    }

}
