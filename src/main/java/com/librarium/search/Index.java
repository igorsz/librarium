package com.librarium.search;

import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Igor on 11.12.2016.
 */

@Value
public class Index {

    private static final Logger logger = LogManager.getLogger(Index.class);
    private String index;

    public Index(String index) throws IndexNameException {
        if(!index.toLowerCase().equals(index)){
            logger.error("Index: {} is not lower case only",index);
            throw new IndexNameException(index);
        }
        this.index = index;
    }
}
