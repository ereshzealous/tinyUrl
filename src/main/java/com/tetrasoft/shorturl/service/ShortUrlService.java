package com.tetrasoft.shorturl.service;

import com.tetrasoft.shorturl.controller.ws.WSShortUrlRequest;
import com.tetrasoft.shorturl.controller.ws.WSShortUrlResponse;
import com.tetrasoft.shorturl.exception.ApplicationException;
import com.tetrasoft.shorturl.persistence.entity.ShortUrl;
import com.tetrasoft.shorturl.persistence.repository.ShortUrlRepository;
import com.tetrasoft.shorturl.util.URLShortenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class ShortUrlService {

    @Autowired
    ShortUrlRepository shortUrlRepository;

    @Autowired
    URLShortenUtil urlShortenUtil;

    @Value("${base.url}")
    String BASE_URL;

    @Value("${shorten.url.prefix}")
    String SHORTEN_URL_PREFIX;

    public WSShortUrlResponse shortenURL(WSShortUrlRequest request) {
        ShortUrl shortUrl = null;
        Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByLongUrl(request.getLongUrl());
        if (shortUrlOptional.isPresent()) {
            shortUrl = shortUrlOptional.get();
            shortUrl = saveAndShorten(request, shortUrl);
        } else {
            shortUrl = saveAndShorten(request, null);
        }
        WSShortUrlResponse response = new WSShortUrlResponse();
        response.setCreatedDate(shortUrl.getCreatedDate());
        response.setExpiryDate(shortUrl.getExpiryDate());
        response.setShortUrl(BASE_URL.concat(SHORTEN_URL_PREFIX).concat(shortUrl.getShortCode()));
        return response;
    }

    public String retrieveLongUrl(String shortCode) throws ApplicationException {
        try {
            Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByShortCode(shortCode);
            if (!shortUrlOptional.isPresent()) {
                throw new ApplicationException(new RuntimeException("Not Found"), "Short Url Not Found", HttpStatus.NOT_FOUND);
            }
            ShortUrl shortUrl = shortUrlOptional.get();
            if (!ZonedDateTime.now().isBefore(shortUrl.getExpiryDate())) {
                throw new ApplicationException(new RuntimeException("Link Expired"), "Short Url Link Expired. Please activate again.", HttpStatus.GONE);
            }
            return shortUrl.getLongUrl();
        } catch (Exception e) {
            if (e instanceof ApplicationException) {
                ApplicationException ae = (ApplicationException) e;
                throw new ApplicationException(e, ae.getMessage(), ae.getStatus());
            }
            throw new ApplicationException(e, "Unexpected Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ShortUrl saveAndShorten(WSShortUrlRequest request, ShortUrl shortUrl) {
        shortUrl = shortUrl == null ? new ShortUrl() : shortUrl;
        shortUrl.setExpiryDays(request.getExpiryDays());
        shortUrl.setExpiryDate(ZonedDateTime.now().plusDays(request.getExpiryDays()));
        shortUrl.setLongUrl(StringUtils.isNotBlank(shortUrl.getLongUrl()) ? shortUrl.getLongUrl() : request.getLongUrl());
        shortUrl = shortUrlRepository.save(shortUrl);
        if (StringUtils.isBlank(shortUrl.getShortCode())) {
            String shortenUrl = urlShortenUtil.generateShortUrl(shortUrl.getId());
            shortUrl.setShortCode(shortenUrl);
            shortUrl = shortUrlRepository.save(shortUrl);
        }
        return shortUrl;
    }

}
