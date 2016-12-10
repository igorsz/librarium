package com.librarium.authentication;

import org.springframework.stereotype.Component;

/**
 * Created by Igor on 08.12.2016.
 */

@Component
public class DummyAuthentication implements Authentication{

    public boolean authenticate() {
        return true;
    }
}
