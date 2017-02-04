package com.librarium.event;

import com.librarium.event.exceptions.IndexNameException;
import lombok.Value;

/**
 * Created by Igor on 11.12.2016.
 */

@Value
public class Index {

    private String index;

    public Index(String index) throws IndexNameException {
        if(!index.toLowerCase().equals(index)){
            throw new IndexNameException(index);
        }
        this.index = index;
    }
}
