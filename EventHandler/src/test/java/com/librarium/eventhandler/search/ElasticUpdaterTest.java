package com.librarium.eventhandler.search;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Igor on 25.04.2017.
 */
public class ElasticUpdaterTest {

    private ElasticUpdater elasticUpdater;
    private RestClient restClient;

    @Before
    public void setUp() {
        restClient = mock(RestClient.class);
        elasticUpdater = new ElasticUpdater(restClient);
    }

    @Test
    public void executeESRequestTest() throws IOException {
        //given
        Response response = mock(Response.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(response.getEntity()).thenReturn(entity);
        InputStream inputStream = new ByteArrayInputStream("TEST".getBytes());
        when(entity.getContent()).thenReturn(inputStream);
        when(restClient.performRequest(anyString(), anyString(), anyMap())).thenReturn(response);
        //when
        String delete = elasticUpdater.executeESRequest("DELETE", "/");
        //then
        assertEquals("TEST", delete);
    }
}
