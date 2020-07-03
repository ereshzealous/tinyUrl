package com.tetrasoft.shorturl.service;

import com.tetrasoft.shorturl.controller.ws.WSShortUrlRequest;
import com.tetrasoft.shorturl.controller.ws.WSShortUrlResponse;
import com.tetrasoft.shorturl.exception.ApplicationException;
import com.tetrasoft.shorturl.persistence.entity.ShortUrl;
import com.tetrasoft.shorturl.persistence.repository.ShortUrlRepository;
import com.tetrasoft.shorturl.util.URLShortenUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestShortUrlService {

    @Mock
    URLShortenUtil mockUrlShortenUtil;

    @Mock
    ShortUrlRepository mockShortUrlRepository;

    @InjectMocks
    ShortUrlService shortUrlService;

    @Before
    public void init() {
        Mockito.when(mockShortUrlRepository.findByLongUrl(Mockito.any())).thenReturn(Optional.of(generateShortUrl(1L,
                "http://google.co.in/", 10, "1bc")));
        Mockito.when(mockUrlShortenUtil.generateShortUrl(Mockito.any())).thenReturn("1bc");
        Mockito.when(mockShortUrlRepository.save(Mockito.any())).thenReturn(generateShortUrl(1L,
                "http://google.co.in/", 10, "1bc"));
        ReflectionTestUtils.setField(shortUrlService, "BASE_URL", "http://localhost:9000/");
    }

    @Test
    public void testShortenURL() {

        // Existed Record
        WSShortUrlRequest request = new WSShortUrlRequest();
        request.setExpiryDays(10);
        request.setLongUrl("http://google.co.in");
        WSShortUrlResponse response = shortUrlService.shortenURL(request);
        assertNotNull(response);
        assertEquals("http://localhost:9000/1bc", response.getShortUrl());
        assertNotNull(response.getCreatedDate());
        long days = ChronoUnit.DAYS.between(response.getCreatedDate(), response.getExpiryDate());
        assertEquals(10, days);

        // New Record
        Mockito.reset(mockShortUrlRepository);
        Mockito.when(mockShortUrlRepository.findByLongUrl(Mockito.eq("https://amazon.in/"))).thenReturn(Optional.empty());
        Mockito.when(mockShortUrlRepository.save(Mockito.any())).thenReturn(generateShortUrl(2L,
                "https://amazon.in/", 100, "abc"));
        Mockito.when(mockUrlShortenUtil.generateShortUrl(Mockito.eq(2L))).thenReturn("abc");
        request.setLongUrl("https://amazon.in/");
        request.setExpiryDays(100);
        response = shortUrlService.shortenURL(request);
        assertNotNull(response);
        assertEquals("http://localhost:9000/abc", response.getShortUrl());
        assertNotNull(response.getCreatedDate());
        days = ChronoUnit.DAYS.between(response.getCreatedDate(), response.getExpiryDate());
        assertEquals(100, days);
    }

    @Test
    public void testRetrieveLongUrl() throws Exception {

        // No record
        Mockito.when(mockShortUrlRepository.findByShortCode(Mockito.eq("xyz"))).thenReturn(Optional.empty());
        try {
            shortUrlService.retrieveLongUrl("xyz");
        } catch (ApplicationException e) {
            assertEquals("Short Url Not Found", e.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertNotNull(e.getCause());
        }

        // Link expired
        Mockito.when(mockShortUrlRepository.findByShortCode(Mockito.eq("abc"))).thenReturn(Optional.of(generateShortUrl(2L,
                "https://amazon.in/", -1, "abc")));
        try {
            shortUrlService.retrieveLongUrl("abc");
        } catch (ApplicationException e) {
            assertEquals("Short Url Link Expired. Please activate again.", e.getMessage());
            assertEquals(HttpStatus.GONE, e.getStatus());
            assertNotNull(e.getCause());
        }

        // Record Retrieved.
        Mockito.when(mockShortUrlRepository.findByShortCode(Mockito.eq("1ac"))).thenReturn(Optional.of(generateShortUrl(1L,
                "https://google.co.in/", 100, "1ac")));
        assertEquals("https://google.co.in/", shortUrlService.retrieveLongUrl("1ac"));
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
}
