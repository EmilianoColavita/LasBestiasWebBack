package com.backend.LasBestias.Security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final Cache<String, Integer> attempts =
            Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build();

    private static final int MAX_REQUESTS = 3;

    public boolean isAllowed(String ip) {

        Integer count = attempts.getIfPresent(ip);

        if (count == null) {
            attempts.put(ip, 1);
            return true;
        }

        if (count >= MAX_REQUESTS) {
            return false;
        }

        attempts.put(ip, count + 1);
        return true;
    }
}