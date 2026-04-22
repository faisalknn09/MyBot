package com.example.MyBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prevents users from spamming !ask
 * Each user can only send 1 request every 5 seconds
 */
@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    // userId -> timestamp of their last command
    private final Map<String, Instant> lastCommandTime = new ConcurrentHashMap<>();

    // Cooldown in seconds
    private static final int COOLDOWN_SECONDS = 5;

    /**
     * Returns true if user is allowed to send a command
     * Returns false if they're sending too fast
     */
    public boolean isAllowed(String userId) {
        Instant now = Instant.now();
        Instant lastTime = lastCommandTime.get(userId);

        if (lastTime == null) {
            lastCommandTime.put(userId, now);
            return true;
        }

        long secondsSinceLast = now.getEpochSecond() - lastTime.getEpochSecond();

        if (secondsSinceLast >= COOLDOWN_SECONDS) {
            lastCommandTime.put(userId, now);
            return true;
        }

        long waitTime = COOLDOWN_SECONDS - secondsSinceLast;
        log.warn("⏳ Rate limit hit for user: {} (wait {}s)", userId, waitTime);
        return false;
    }

    /**
     * How many seconds until user can send again
     */
    public long getWaitTime(String userId) {
        Instant lastTime = lastCommandTime.get(userId);
        if (lastTime == null) return 0;
        long secondsSinceLast = Instant.now().getEpochSecond() - lastTime.getEpochSecond();
        return Math.max(0, COOLDOWN_SECONDS - secondsSinceLast);
    }
}