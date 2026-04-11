package com.example.spareseat.model;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EventCreateRequestTest {

    private final Gson gson = new Gson();

    // ── Constructor & getters ─────────────────────────────────────

    @Test
    public void allFields_setCorrectlyViaConstructor() {
        EventCreateRequest r = new EventCreateRequest(
                7L, "Gala Night", "Annual gala", "2026-08-20",
                "Montreal", "Social", 150);

        assertEquals(Long.valueOf(7L), r.getOrganizerId());
        assertEquals("Gala Night", r.getTitle());
        assertEquals("Annual gala", r.getDescription());
        assertEquals("2026-08-20", r.getDate());
        assertEquals("Montreal", r.getLocation());
        assertEquals("Social", r.getCategory());
        assertEquals(150, r.getEventCapacity());
    }

    @Test
    public void nullDescription_storedAsNull() {
        EventCreateRequest r = new EventCreateRequest(
                1L, "Meetup", null, "2026-04-10", "Ottawa", "Tech", 50);

        assertNull(r.getDescription());
    }

    @Test
    public void nullCategory_storedAsNull() {
        EventCreateRequest r = new EventCreateRequest(
                1L, "Workshop", "Desc", "2026-05-01", "Toronto", null, 30);

        assertNull(r.getCategory());
    }

    // ── Gson serialization ────────────────────────────────────────

    @Test
    public void serializedToJson_containsAllFields() {
        EventCreateRequest r = new EventCreateRequest(
                3L, "Tech Talk", "Discuss AI", "2026-09-15",
                "Vancouver", "Tech", 75);

        String json = gson.toJson(r);

        assertEquals(true, json.contains("\"organizerId\":3"));
        assertEquals(true, json.contains("\"title\":\"Tech Talk\""));
        assertEquals(true, json.contains("\"date\":\"2026-09-15\""));
        assertEquals(true, json.contains("\"location\":\"Vancouver\""));
        assertEquals(true, json.contains("\"eventCapacity\":75"));
    }

    @Test
    public void serializedToJson_nullCategoryIncluded() {
        EventCreateRequest r = new EventCreateRequest(
                2L, "Event", null, "2026-03-01", "Montreal", null, 20);

        String json = new Gson().newBuilder().serializeNulls().create().toJson(r);

        assertEquals(true, json.contains("\"category\":null"));
        assertEquals(true, json.contains("\"description\":null"));
    }
}
