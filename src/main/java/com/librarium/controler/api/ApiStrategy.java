package com.librarium.controler.api;

import com.librarium.persistance.DocumentAlreadyExistsException;
import com.librarium.persistance.DocumentNotExistsException;
import com.librarium.search.FullDocumentPath;
import com.librarium.search.Index;
import com.librarium.search.Type;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Igor on 08.12.2016.
 */
public interface ApiStrategy {
    void search(JSONObject search, OutputStream outputStream);

    void search(JSONObject search, OutputStream outputStream, List<Index> namespacesList);

    void search(JSONObject search, OutputStream outputStream, List<Index> indexList, List<Type> typeList);

    void createIndex(Index index, JSONObject body, OutputStream outputStream);

    void createIndex(Index index, OutputStream outputStream);

    void putDocument(FullDocumentPath fullDocumentPath, MultipartFile file, String metadata, String transformations) throws IOException, DocumentAlreadyExistsException;

    void deleteDocument(FullDocumentPath fullDocumentPath) throws DocumentNotExistsException;

    void updateDocument(FullDocumentPath fullDocumentPath, String metadata) throws DocumentNotExistsException;

    void listIndices(OutputStream outputStream);
}
