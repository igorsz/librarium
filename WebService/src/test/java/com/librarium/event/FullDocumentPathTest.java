package com.librarium.event;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Igor on 07.02.2017.
 */
public class FullDocumentPathTest {

    private String INDEX = "index";
    private String TYPE = "type";
    private String ID = "id";
    private String EXPECTED_VALUE = "index/type/id";

    @Test
    public void receiveCorrectFullDocumentPath(){
        FullDocumentPath fullDocumentPath = new FullDocumentPath(INDEX,TYPE,ID);
        assertTrue(fullDocumentPath.getFullPath().equals(EXPECTED_VALUE));
    }
}
