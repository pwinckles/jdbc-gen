package com.pwinckles.jdbcgen.sort;

/**
 * The direction to sort query results.
 */
enum Direction {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
