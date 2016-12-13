package com.librarium.controler.api;

import com.librarium.search.Index;
import com.librarium.search.Type;
import org.json.simple.JSONObject;

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
}
