package com.tetrasoft.shorturl.controller.ws;

import com.tetrasoft.shorturl.rest.common.RestResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WSShortUrlResponse extends RestResponse {
    private String shortUrl;
    private String longUrl;
    private ZonedDateTime createdDate;
    private ZonedDateTime expiryDate;
}
