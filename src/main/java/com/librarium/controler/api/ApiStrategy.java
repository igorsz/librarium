package com.librarium.controler.api;

import com.librarium.search.Namespace;
import org.json.simple.JSONObject;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by Igor on 08.12.2016.
 */
public interface ApiStrategy {
    void search(JSONObject search, OutputStream outputStream);

    void search(JSONObject search, OutputStream outputStream, List<Namespace> namespacesList);
}
