package com.librarium.event;

import com.google.gson.JsonObject;
import com.librarium.event.exceptions.StringToJsonMappingException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Igor on 07.02.2017.
 */
public class StringToJsonMapperTest {

    private JsonObject expectedJson;
    private String CORRECT_STRING_VALUE = "{\"testKey\":\"testValue\"}";
    private String INCORRECT_STRING_VALUE = "{\"testKey:\"testValue\"}";
    private StringToJsonMapper stringToJsonMapper;

    @Before
    public void setUp(){
        this.stringToJsonMapper = new StringToJsonMapper();
        expectedJson = new JsonObject();
        expectedJson.addProperty("testKey","testValue");
    }

    @Test
    public void getCorrectJsonFromString() throws StringToJsonMappingException {
        JsonObject jsonFromString = stringToJsonMapper.getJsonFromString(CORRECT_STRING_VALUE);
        assertTrue(jsonFromString.equals(expectedJson));
    }

    @Test(expected = StringToJsonMappingException.class)
    public void getExceptionWhenIncorrectStringProvided() throws StringToJsonMappingException {
        stringToJsonMapper.getJsonFromString(INCORRECT_STRING_VALUE);
    }
}
