package com.es.phoneshop.security;

import com.es.phoneshop.security.impl.DefaultDosProtectionService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultDosProtectionServiceTest {
    private DosProtectionService dosProtectionService;

    @Before
    public void setup() {
        dosProtectionService = DefaultDosProtectionService.getInstance();
    }

    @Test
    public void isAllowed_withinLimit_ReturnsTrue() {
        String ip = "127.0.0.1";

        boolean isAllowed = dosProtectionService.isAllowed(ip);

        assertTrue(isAllowed);
    }

    @Test
    public void isAllowed_exceedsLimit_ReturnsFalse() {
        String ip = "127.0.0.2";
        simulateExcessiveRequests(ip);

        boolean isAllowed = dosProtectionService.isAllowed(ip);

        assertFalse(isAllowed);
    }

    private void simulateExcessiveRequests(String ip) {
        int amountOfRequests = 30;
        for (int i = 0; i < amountOfRequests; i++) {
            dosProtectionService.isAllowed(ip);
        }
    }

}
