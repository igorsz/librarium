package com.librarium.controler;

import com.librarium.controler.api.ApiDispatcher;
import com.librarium.persistance.exceptions.DocumentAlreadyExistsException;
import com.librarium.persistance.exceptions.DocumentNotExistsException;
import com.librarium.search.FullDocumentPath;
import com.librarium.search.Index;
import com.librarium.search.exceptions.IndexNameException;
import com.librarium.search.Type;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        apiDispatcher.search(search, outputStream);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @PostMapping(value = "/{indices}/_search", produces = "application/json")
    public ResponseEntity<String> search(@PathVariable List<String> indices,
                                         @RequestBody JSONObject search) throws IndexNameException {
        List<Index> indexList = prepareIndicesList(indices);
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search, outputStream, indexList);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @PostMapping(value = "/{indices}/{types}/_search", produces = "application/json")
    public ResponseEntity<String> search(@PathVariable List<String> indices,
                                         @PathVariable List<String> types,
                                         @RequestBody JSONObject search) throws IndexNameException {
        List<Index> indexList = prepareIndicesList(indices);
        List<Type> typeList = prepareTypesList(types);
        OutputStream outputStream = new ByteArrayOutputStream();
        apiDispatcher.search(search, outputStream, indexList, typeList);
        return new ResponseEntity<String>(outputStream.toString(), getJsonHttpHeader(), HttpStatus.OK);
    }

    @PutMapping(value = "/{index}/{type}/{documentId}", produces = "application/json")
    public ResponseEntity<String> putDocument(@PathVariable String index,
                                              @PathVariable String type,
                                              @PathVariable String documentId,
                                              @RequestParam MultipartFile file,
                                              @RequestParam(required = false) Map<String,String> params)
            throws IOException, DocumentAlreadyExistsException {
        String metadata = params.get("metadata");
        String transformations = params.get("transformations");
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            apiDispatcher.putDocument(new FullDocumentPath(index, type, documentId), file, metadata, transformations);
            return new ResponseEntity<String>(new String(bytes), getJsonHttpHeader(), HttpStatus.OK);
        }
        return new ResponseEntity<String>("no document provided", getJsonHttpHeader(), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{index}/{type}/{documentId}", produces = "application/json")
    public ResponseEntity<String> deleteDocument(@PathVariable String index,
                                                 @PathVariable String type,
                                                 @PathVariable String documentId)
            throws DocumentNotExistsException {
        apiDispatcher.deleteDocument(new FullDocumentPath(index, type, documentId));
        return new ResponseEntity<String>("ok", getJsonHttpHeader(), HttpStatus.OK);
    }

    @PostMapping(value = "/{index}/{type}/{documentId}", produces = "application/json")
    public ResponseEntity<String> updateDocument(@PathVariable String index,
                                                 @PathVariable String type,
                                                 @PathVariable String documentId,
                                                 @RequestParam(required = false) Map<String,String> params)
            throws DocumentNotExistsException {
        String metadata = params.get("metadata");
        apiDispatcher.updateDocument(new FullDocumentPath(index, type, documentId), metadata);
        return new ResponseEntity<String>("ok", getJsonHttpHeader(), HttpStatus.OK);
    }

    private List<Type> prepareTypesList(List<String> types) {
        List<Type> typeList = new ArrayList<Type>();
        for (String type : types) typeList.add(new Type(type));
        return typeList;
    }

    private List<Index> prepareIndicesList(List<String> indices) throws IndexNameException {
        List<Index> indicesList = new ArrayList<Index>();
        for (String namespace : indices) indicesList.add(new Index(namespace));
        return indicesList;
    }

    private HttpHeaders getJsonHttpHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
