package com.librarium.common.event;

import lombok.Value;

/**
 * Created by Igor on 07.01.2017.
 */
@Value
public class FullDocumentPath {
    
    private String fullPath;

    public FullDocumentPath(String index, String type, String documentId) {
        this.fullPath = index+"/"+type+"/"+documentId;
    }
}
