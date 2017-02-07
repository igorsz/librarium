package com.librarium.event;

import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.librarium.event.exceptions.StringToJsonMappingException;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 07.02.2017.
 */

@Component
public class StringToJsonMapper {

    JsonParser parser;

    public StringToJsonMapper() {
        this.parser = new JsonParser();
    }

    public JsonObject getJsonFromString(String string) throws StringToJsonMappingException {
        JsonObject parsed;
        try {
            parsed = (JsonObject) parser.parse(string);
        } catch (Exception e){
            throw new StringToJsonMappingException(string);
        }
        return parsed;
    }
}
