package com.librarium.event;

import com.librarium.event.exceptions.IndexNameException;
import org.junit.Test;

/**
 * Created by Igor on 07.02.2017.
 */
public class IndexTest {

    private String INVALID_STRING_NAME="Test";

    @Test(expected = IndexNameException.class)
    public void testIndexNameException() throws IndexNameException {
        Index index = new Index(INVALID_STRING_NAME);
    }
}
