package com.pwinckles.jdbcgen;

public enum OrderDirection {

    ASCENDING("ASC"),
    DESCENDING("DESC");

    private final String value;

    OrderDirection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
