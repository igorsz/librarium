package com.librarium.common.event;

import com.librarium.common.event.exceptions.IndexNameException;
import lombok.Value;

/**
 * Created by Igor on 07.01.2017.
 */
@Value
public class FullDocumentPath {
    
    private String fullPath;
    private Index index;
    private Type type;
    private String documentId;

    public FullDocumentPath(String index, String type, String documentId) throws IndexNameException {
        this.fullPath = index+"/"+type+"/"+documentId;
        this.index = new Index(index);
        this.type = new Type(type);
        this.documentId = documentId;
    }
}
