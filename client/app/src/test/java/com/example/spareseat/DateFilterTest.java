package com.example.spareseat;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateFilterTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private String formatDate(Calendar cal) {
        return SDF.format(cal.getTime());
    }

    private String today() {
        return formatDate(Calendar.getInstance());
    }

    private String daysFromNow(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return formatDate(cal);
    }

    private String monthsFromNow(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        return formatDate(cal);
    }

    // ── "All" filter ──────────────────────────────────────────────

    @Test
    public void all_matchesAnyDate() {
        assertTrue(LoggedInActivity.matchesDateFilter("All", today()));
        assertTrue(LoggedInActivity.matchesDateFilter("All", "2020-01-01"));
    }

    @Test
    public void all_matchesNullDate() {
        assertTrue(LoggedInActivity.matchesDateFilter("All", null));
    }

    @Test
    public void nullFilter_treatedAsAll() {
        assertTrue(LoggedInActivity.matchesDateFilter(null, today()));
    }

    // ── "Today" filter ────────────────────────────────────────────

    @Test
    public void today_matchesToday() {
        assertTrue(LoggedInActivity.matchesDateFilter("Today", today()));
    }

    @Test
    public void today_doesNotMatchYesterday() {
        assertFalse(LoggedInActivity.matchesDateFilter("Today", daysFromNow(-1)));
    }

    @Test
    public void today_doesNotMatchTomorrow() {
        assertFalse(LoggedInActivity.matchesDateFilter("Today", daysFromNow(1)));
    }

    // ── "This Week" filter ────────────────────────────────────────

    @Test
    public void thisWeek_matchesToday() {
        assertTrue(LoggedInActivity.matchesDateFilter("This Week", today()));
    }

    @Test
    public void thisWeek_doesNotMatchLastWeek() {
        assertFalse(LoggedInActivity.matchesDateFilter("This Week", daysFromNow(-7)));
    }

    @Test
    public void thisWeek_doesNotMatchNextWeek() {
        assertFalse(LoggedInActivity.matchesDateFilter("This Week", daysFromNow(7)));
    }

    // ── "This Month" filter ───────────────────────────────────────

    @Test
    public void thisMonth_matchesToday() {
        assertTrue(LoggedInActivity.matchesDateFilter("This Month", today()));
    }

    @Test
    public void thisMonth_doesNotMatchLastMonth() {
        assertFalse(LoggedInActivity.matchesDateFilter("This Month", monthsFromNow(-1)));
    }

    @Test
    public void thisMonth_doesNotMatchNextMonth() {
        assertFalse(LoggedInActivity.matchesDateFilter("This Month", monthsFromNow(1)));
    }

    // ── Edge cases ────────────────────────────────────────────────

    @Test
    public void nonAllFilter_nullDate_returnsFalse() {
        assertFalse(LoggedInActivity.matchesDateFilter("Today", null));
        assertFalse(LoggedInActivity.matchesDateFilter("This Week", null));
        assertFalse(LoggedInActivity.matchesDateFilter("This Month", null));
    }

    @Test
    public void nonAllFilter_emptyDate_returnsFalse() {
        assertFalse(LoggedInActivity.matchesDateFilter("Today", ""));
    }

    @Test
    public void nonAllFilter_malformedDate_returnsFalse() {
        assertFalse(LoggedInActivity.matchesDateFilter("Today", "not-a-date"));
        assertFalse(LoggedInActivity.matchesDateFilter("This Month", "28-03-2026"));
    }
}
