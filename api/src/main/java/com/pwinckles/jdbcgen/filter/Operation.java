package com.pwinckles.jdbcgen.filter;

/**
 * A predicate's comparison operation
 */
enum Operation {
    EQUAL("="),
    NOT_EQUAL("!="),
    EQUAL_INSENSITIVE("="),
    NOT_EQUAL_INSENSITIVE("!="),
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

    /**
     * @return the SQL representation of the operation
     */
    public String getSymbol() {
        return symbol;
    }
}
