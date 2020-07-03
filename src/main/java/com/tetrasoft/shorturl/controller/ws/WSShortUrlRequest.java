package com.tetrasoft.shorturl.controller.ws;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Data
public class WSShortUrlRequest {
    @NotBlank(message = "{request.long.url.can.not.be.empty}")
    @URL(message = "{request.long.url.invalid.format}")
    private String longUrl;

    @Range(min = 1, max = 90, message = "{request.expiry.days.not.in.range}")
    private Integer expiryDays = 1;
}
