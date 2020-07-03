package com.tetrasoft.shorturl.util;

import com.tetrasoft.shorturl.exception.ApplicationException;
import com.tetrasoft.shorturl.exception.RestFault;
import com.tetrasoft.shorturl.rest.common.RestApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TestRestControllerAdvice {

    @InjectMocks
    RestControllerAdvice restControllerAdvice;

    @Test
    public void testHandleException() {
        ResponseEntity<RestFault> responseEntity = restControllerAdvice.handleException(new RuntimeException());
        assertNotNull(responseEntity);
        RestFault restFault = responseEntity.getBody();
        assertNotNull(restFault);
        assertEquals("Unexpected error occurred", restFault.getErrorMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleARestException() {
        RestApiException apiException = new RestApiException("Message", RestApiException.createApplicationExceptionFault(new ApplicationException(new RuntimeException("Link Expired"),
                "Short Url Link Expired. Please activate again.", HttpStatus.GONE)));
        ResponseEntity<RestFault> responseEntity = restControllerAdvice.handleARestException(apiException);
        assertNotNull(responseEntity);
        RestFault restFault = responseEntity.getBody();
        assertNotNull(restFault);
        assertEquals("Short Url Link Expired. Please activate again.", restFault.getErrorMessage());
        assertEquals(HttpStatus.GONE, responseEntity.getStatusCode());
    }

}
