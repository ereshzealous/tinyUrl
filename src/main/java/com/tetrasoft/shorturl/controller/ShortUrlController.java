package com.tetrasoft.shorturl.controller;

import com.tetrasoft.shorturl.controller.ws.WSShortUrlRequest;
import com.tetrasoft.shorturl.exception.ApplicationException;
import com.tetrasoft.shorturl.persistence.entity.ShortUrl;
import com.tetrasoft.shorturl.persistence.repository.ShortUrlRepository;
import com.tetrasoft.shorturl.rest.common.BaseRestApi;
import com.tetrasoft.shorturl.rest.common.RestApiException;
import com.tetrasoft.shorturl.rest.common.RestResponse;
import com.tetrasoft.shorturl.service.ShortUrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/")
public class ShortUrlController extends BaseRestApi {

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    ShortUrlRepository shortUrlRepository;

    @PostMapping
    public ResponseEntity<RestResponse> shortenUrl(@Valid @RequestBody WSShortUrlRequest request) throws RestApiException {
        return inboundServiceCall(request, service -> {
            return ResponseEntity.ok(shortUrlService.shortenURL(request));
        });
    }

    @GetMapping("all")
    public ResponseEntity<List<ShortUrl>> getAll() {
        return ResponseEntity.ok(shortUrlRepository.findAll());
    }

    @GetMapping("{code}")
    public ModelAndView redirectToOriginalUrl(@PathVariable("code") String code) throws RestApiException {
        return modelAndViewServiceCall(new Object(), service -> {
            String longUrl = shortUrlService.retrieveLongUrl(code);
            if (StringUtils.isNotBlank(longUrl)) {
                return  new ModelAndView("redirect:" + longUrl);
            }
            return new ModelAndView("tiny_not_found");
        });
    }
}
