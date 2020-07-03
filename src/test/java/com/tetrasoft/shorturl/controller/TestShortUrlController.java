package com.tetrasoft.shorturl.controller;

import com.tetrasoft.shorturl.controller.ws.WSShortUrlRequest;
import com.tetrasoft.shorturl.controller.ws.WSShortUrlResponse;
import com.tetrasoft.shorturl.exception.ApplicationException;
import com.tetrasoft.shorturl.persistence.entity.ShortUrl;
import com.tetrasoft.shorturl.persistence.repository.ShortUrlRepository;
import com.tetrasoft.shorturl.rest.common.RestApiException;
import com.tetrasoft.shorturl.rest.common.RestResponse;
import com.tetrasoft.shorturl.service.ShortUrlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestShortUrlController {

    @Mock
    ShortUrlService mockShortUrlService;

    @Mock
    ShortUrlRepository mockShortUrlRepository;

    @InjectMocks
    ShortUrlController shortUrlController;

    @Test
    public void testShortenUrl() throws RestApiException {
        WSShortUrlRequest request = new WSShortUrlRequest();
        request.setExpiryDays(10);
        request.setLongUrl("https://google.co.in/");
        Mockito.when(mockShortUrlService.shortenURL(Mockito.eq(request))).thenReturn(generateUrlResponse("http://localhost:9000/1bc", 10));
        ResponseEntity<RestResponse> responseEntity = shortUrlController.shortenUrl(request);
        assertNotNull(responseEntity);
        WSShortUrlResponse response = (WSShortUrlResponse) responseEntity.getBody();
        assertNotNull(response);
        assertEquals("http://localhost:9000/1bc", response.getShortUrl());
        assertNotNull(response.getCreatedDate());
        assertNotNull(response.getExpiryDate());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetAll() {
        List<ShortUrl> shortUrls = LongStream.range(1, 11).mapToObj(index -> generateShortUrl(index, "https://google.co.in" + index,
                10, index + "abc")).collect(Collectors.toList());
        Mockito.when(mockShortUrlRepository.findAll()).thenReturn(shortUrls);
        ResponseEntity<List<ShortUrl>> responseEntity = shortUrlController.getAll();
        assertNotNull(responseEntity);
        List<ShortUrl> urls = responseEntity.getBody();
        assertEquals(10, urls.size());
        assertEquals(shortUrls, urls);
    }

    @Test
    public void testRedirectToOriginalUrl() throws ApplicationException, RestApiException {
        // Exception
        Mockito.when(mockShortUrlService.retrieveLongUrl(Mockito.eq("xyz"))).thenThrow(new ApplicationException(new RuntimeException
                ("Link Expired"), "Short Url Link Expired. Please activate again.", HttpStatus.GONE));
        try {
            shortUrlController.redirectToOriginalUrl("xyz") ;
        } catch (RestApiException e) {
            assertEquals("Short Url Link Expired. Please activate again.", e.getMessage());
        }

        // without Exception.
        Mockito.when(mockShortUrlService.retrieveLongUrl(Mockito.eq("abc"))).thenReturn("https://google.co.in/");
        ModelAndView modelAndView = shortUrlController.redirectToOriginalUrl("abc");
        assertNotNull(modelAndView);
        assertEquals(HttpStatus.OK, modelAndView.getStatus());
        assertTrue(modelAndView.hasView());
        assertEquals("redirect:https://google.co.in/", modelAndView.getViewName());
    }

    private ShortUrl generateShortUrl(Long id, String longUrl, Integer expiryDays, String shortCode) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortCode(shortCode);
        shortUrl.setId(id);
        shortUrl.setLongUrl(longUrl);
        shortUrl.setExpiryDays(expiryDays);
        shortUrl.setExpiryDate(ZonedDateTime.now().plusDays(expiryDays));
        shortUrl.setCreatedDate(ZonedDateTime.now());
        return shortUrl;
    }

    private WSShortUrlResponse generateUrlResponse(String shortUrl, Integer expiryDays) {
        WSShortUrlResponse response = new WSShortUrlResponse();
        response.setShortUrl(shortUrl);
        response.setExpiryDate(ZonedDateTime.now().plusDays(expiryDays));
        response.setCreatedDate(ZonedDateTime.now());
        return response;
    }
}
