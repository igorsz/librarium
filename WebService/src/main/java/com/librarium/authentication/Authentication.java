package com.librarium.authentication;


import com.librarium.common.event.FullDocumentPath;

/**
 * Created by Igor on 08.12.2016.
 */
public interface Authentication {
    boolean authenticate();

    boolean authenticate(FullDocumentPath path);

}
