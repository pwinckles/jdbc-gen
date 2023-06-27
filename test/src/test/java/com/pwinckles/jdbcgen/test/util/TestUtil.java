package com.pwinckles.jdbcgen.test.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public final class TestUtil {

    private TestUtil() {}

    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static ZonedDateTime nowZonedDateTime() {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static OffsetDateTime nowOffsetDateTime() {
        return OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

}
