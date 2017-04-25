package com.librarium.search;

import com.librarium.authentication.DummyAuthentication;
import com.librarium.common.event.FullDocumentPath;
import com.librarium.common.event.Index;
import com.librarium.common.event.Type;
import com.librarium.common.event.exceptions.IndexNameException;
import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class ElasticsearchTest {

    private Elasticsearch elasticsearch;
    private RestClient restClient;
    private DummyAuthentication authentication;

    @Before
    public void setUp() {
        restClient = mock(RestClient.class);
        authentication = mock(DummyAuthentication.class);
        elasticsearch = new Elasticsearch(restClient, authentication);
    }

    @Test
    public void executeESRequestTest() throws IOException {
        //given
        OutputStream outputStream = new ByteArrayOutputStream();
        Response response = mock(Response.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(response.getEntity()).thenReturn(entity);
        InputStream inputStream = new ByteArrayInputStream("TEST".getBytes());
        when(entity.getContent()).thenReturn(inputStream);
        when(restClient.performRequest(anyString(), anyString(), anyMap())).thenReturn(response);
        //when
        elasticsearch.executeESRequest("POST", "/", outputStream);
        //then
        assertEquals("TEST", outputStream.toString());
    }

    @Test
    public void prepareIndexStringTest() throws IndexNameException {
        //given
        List<Index> indicesList = new ArrayList<Index>();
        indicesList.add(new Index("index1"));
        indicesList.add(new Index("index2"));
        //when
        String indices = elasticsearch.prepareIndexString(indicesList);
        //then
        assertEquals("index1,index2", indices);
    }

    @Test
    public void prepareTypeStringTest() {
        //given
        List<Type> typesList = new ArrayList<Type>();
        typesList.add(new Type("type1"));
        typesList.add(new Type("type2"));
        //when
        String types = elasticsearch.prepareTypeString(typesList);
        //then
        assertEquals("type1,type2", types);
    }

    @Test
    public void checkResponseEntitlmentTest() {
        //given
        String responseString = prepareElasticsearchResponse();
        when(authentication.authenticate(any(FullDocumentPath.class))).thenReturn(true);
        //when
        String responseWithEntitlment = elasticsearch.checkResponseEntitlment(responseString);
        //then
        assertEquals(expectedResponseString(), responseWithEntitlment);
    }

    private String expectedResponseString() {
        return "[{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_source\":{\"date\":\"2009-11-15T14:12:12\",\"message\":\"" +
                "trying out Elasticsearch\",\"user\":\"kimchy\",\"likes\":0},\"_id\":\"0\",\"_score\":1.3862944}]";

    }

    private String prepareElasticsearchResponse() {
        return "{\n" +
                "    \"took\": 1,\n" +
                "    \"timed_out\": false,\n" +
                "    \"_shards\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"successful\" : 1,\n" +
                "        \"failed\" : 0\n" +
                "    },\n" +
                "    \"hits\":{\n" +
                "        \"total\" : 1,\n" +
                "        \"max_score\": 1.3862944,\n" +
                "        \"hits\" : [\n" +
                "            {\n" +
                "                \"_index\" : \"twitter\",\n" +
                "                \"_type\" : \"tweet\",\n" +
                "                \"_id\" : \"0\",\n" +
                "                \"_score\": 1.3862944,\n" +
                "                \"_source\" : {\n" +
                "                    \"user\" : \"kimchy\",\n" +
                "                    \"message\": \"trying out Elasticsearch\",\n" +
                "                    \"date\" : \"2009-11-15T14:12:12\",\n" +
                "                    \"likes\" : 0\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
    }
}
