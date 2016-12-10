package com.librarium.controler.api;

import com.librarium.authentication.Authentication;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * Created by Igor on 08.12.2016.
 */

@Component
public class ApiDispatcher {

    @Autowired
    ApiStrategy apiStrategy;

    @Autowired
    Authentication authentication;

    private ApiStrategy getApiStrategy() {
        return apiStrategy;
    }


    public void search(JSONObject search, OutputStream outputStream) {
        authentication.authenticate();
        getApiStrategy().search(search,outputStream);
    }
}
