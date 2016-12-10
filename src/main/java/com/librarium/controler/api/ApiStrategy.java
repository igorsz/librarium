package com.librarium.controler.api;

import org.json.simple.JSONObject;

import java.io.OutputStream;

/**
 * Created by Igor on 08.12.2016.
 */
public interface ApiStrategy {
    void search(JSONObject search, OutputStream outputStream);
}
