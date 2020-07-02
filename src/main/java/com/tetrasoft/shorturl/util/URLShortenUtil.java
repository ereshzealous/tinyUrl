package com.tetrasoft.shorturl.util;

import com.tetrasoft.shorturl.dto.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class URLShortenUtil {

    public String generateShortUrl(Long id) {
        String UID = UUID.randomUUID().toString().replaceAll("-", "");
        ID idObject = new ID(id);
        String numbersOnly = UID.replaceAll("[^0-9]", "");
        numbersOnly = numbersOnly.length() > 5 ? StringUtils.substring(numbersOnly, 0, 5) : numbersOnly;
        id = idObject.hashCode() + id + Integer.parseInt(numbersOnly);
        StringBuilder key = new StringBuilder(32);
        while (id > 0) {
            Integer value = 0;
            Integer intValue = id.intValue();
            value = intValue < 0 ? 0 - intValue : intValue;
            key.insert(0, UID.charAt((value % UID.length())));
            id /= UID.length();
        }
        return key.toString();
    }
}
