package com.pwinckles.jdbcgen.filter;

// TODO javadoc
enum Operation {
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    LIKE_INSENSITIVE("LIKE"),
    NOT_LIKE_INSENSITIVE("NOT LIKE");

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
