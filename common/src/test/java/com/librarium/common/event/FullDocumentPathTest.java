package com.librarium.common.event;

import junit.framework.TestCase;
import org.junit.Test;


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
        TestCase.assertTrue(fullDocumentPath.getFullPath().equals(EXPECTED_VALUE));
    }
}
