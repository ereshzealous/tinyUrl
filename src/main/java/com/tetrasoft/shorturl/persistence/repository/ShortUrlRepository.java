package com.tetrasoft.shorturl.persistence.repository;

import com.tetrasoft.shorturl.persistence.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByLongUrl(String url);
    Optional<ShortUrl> findByShortCode(String code);
}
