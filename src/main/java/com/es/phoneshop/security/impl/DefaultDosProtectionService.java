package com.es.phoneshop.security.impl;

import com.es.phoneshop.security.DosProtectionService;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final long AMOUNT_OF_REQUEST = 20;
    private static final long TIME_INTERVAL = 60;
    private static volatile DosProtectionService instance;
    private final Map<String, Long> ipRequestCount = new ConcurrentHashMap<>();
    private final Map<String, Long> ipRequestTime = new ConcurrentHashMap<>();

    public static DosProtectionService getInstance() {
        if (instance == null) {
            synchronized (DefaultDosProtectionService.class) {
                if (instance == null) {
                    instance = new DefaultDosProtectionService();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean isAllowed(String ip) {
        if (isAllowedByTimeInterval(ip)) {
            return true;
        }
        return isAllowedByRequestCount(ip);
    }

    private boolean isAllowedByRequestCount(String ip) {
        Long count = ipRequestCount.get(ip);
        if (count < AMOUNT_OF_REQUEST) {
            ipRequestCount.put(ip, count + 1);
            return true;
        }
        return false;
    }

    private boolean isAllowedByTimeInterval(String ip) {
        Long requestTime = ipRequestTime.get(ip);
        if (requestTime == null || isTimeIntervalPassed(requestTime)) {
            ipRequestCount.put(ip, 0L);
            ipRequestTime.put(ip, getCurrentTimeInSeconds());
            return true;
        }
        return false;
    }

    private boolean isTimeIntervalPassed(Long lastRequestTime) {
        return getCurrentTimeInSeconds() - lastRequestTime > TIME_INTERVAL;
    }

    private Long getCurrentTimeInSeconds() {
        return Instant.now().getEpochSecond();
    }

}
