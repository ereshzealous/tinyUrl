package com.tetrasoft.shorturl.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TestUrlShortenUtil {

    @InjectMocks
    URLShortenUtil urlShortenUtil;

    @Test
    public void testGenerateShortUrl() {
        assertNotNull(urlShortenUtil.generateShortUrl(1L));
    }
}
