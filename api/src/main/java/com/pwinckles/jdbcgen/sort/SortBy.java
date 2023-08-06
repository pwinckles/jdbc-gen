package com.pwinckles.jdbcgen.sort;

import java.util.Objects;

/**
 * Represents a column to sort on
 */
class SortBy {

    private final String columnName;
    private final Direction direction;

    /**
     * Ascending sort on the specified column
     *
     * @param columnName the name of the column to sort on
     * @return SortBy
     */
    public static SortBy asc(String columnName) {
        return new SortBy(columnName, Direction.ASCENDING);
    }

    /**
     * Descending sort on the specified column
     *
     * @param columnName the name of the column to sort on
     * @return SortBy
     */
    public static SortBy desc(String columnName) {
        return new SortBy(columnName, Direction.DESCENDING);
    }

    private SortBy(String columnName, Direction direction) {
        this.columnName = Objects.requireNonNull(columnName, "columnName cannot be null");
        this.direction = Objects.requireNonNull(direction, "direction cannot be null");
    }

    /**
     * Write to the query
     *
     * @param queryBuilder the query builder to append to
     */
    public void buildQuery(StringBuilder queryBuilder) {
        queryBuilder.append(columnName).append(" ").append(direction.getValue());
    }
}
