package com.example.spareseat;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventFormValidationTest {

    // ── Valid formats ─────────────────────────────────────────────

    @Test
    public void validDate_isoFormat_returnsTrue() {
        assertTrue(HostDashboardActivity.isValidDateFormat("2026-04-15"));
    }

    @Test
    public void validDate_startOfYear_returnsTrue() {
        assertTrue(HostDashboardActivity.isValidDateFormat("2026-01-01"));
    }

    @Test
    public void validDate_endOfYear_returnsTrue() {
        assertTrue(HostDashboardActivity.isValidDateFormat("2026-12-31"));
    }

    @Test
    public void validDate_pastYear_returnsTrue() {
        assertTrue(HostDashboardActivity.isValidDateFormat("2020-06-30"));
    }

    // ── Wrong separator / ordering ────────────────────────────────

    @Test
    public void invalidDate_ddMmYyyy_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("15-04-2026"));
    }

    @Test
    public void invalidDate_slashSeparator_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("2026/04/15"));
    }

    @Test
    public void invalidDate_noSeparator_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("20260415"));
    }

    // ── Wrong digit counts ────────────────────────────────────────

    @Test
    public void invalidDate_twoDigitYear_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("26-04-15"));
    }

    @Test
    public void invalidDate_singleDigitMonth_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("2026-4-15"));
    }

    @Test
    public void invalidDate_singleDigitDay_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("2026-04-5"));
    }

    // ── Null / empty ──────────────────────────────────────────────

    @Test
    public void nullDate_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat(null));
    }

    @Test
    public void emptyDate_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat(""));
    }

    @Test
    public void whitespaceDate_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("   "));
    }

    // ── Non-numeric / garbage ─────────────────────────────────────

    @Test
    public void invalidDate_plainText_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("not-a-date"));
    }

    @Test
    public void invalidDate_partiallyNumeric_returnsFalse() {
        assertFalse(HostDashboardActivity.isValidDateFormat("2026-AB-15"));
    }
}
