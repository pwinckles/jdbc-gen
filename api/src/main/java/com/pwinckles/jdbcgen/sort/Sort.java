package com.pwinckles.jdbcgen.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a SQL ORDER BY clause
 */
public class Sort {

    private final List<SortBy> sortByColumns = new ArrayList<>();

    /**
     * Adds a column to sort on in an ascending direction
     *
     * @param columnName the name of the column to sort on
     */
    public void asc(String columnName) {
        sortByColumns.add(SortBy.asc(columnName));
    }

    /**
     * Adds a column to sort on in a descending direction
     *
     * @param columnName the name of the column to sort on
     */
    public void desc(String columnName) {
        sortByColumns.add(SortBy.desc(columnName));
    }

    /**
     * Transforms the accumulated columns to sort on into a SQL SORT BY clause and appends it to the query. If there
     * are no columns, then nothing is appended.
     *
     * @param queryBuilder the query builder to append to
     */
    public void buildQuery(StringBuilder queryBuilder) {
        if (sortByColumns.isEmpty()) {
            return;
        }

        queryBuilder.append(" ORDER BY ");

        for (var it = sortByColumns.iterator(); it.hasNext(); ) {
            var part = it.next();
            part.buildQuery(queryBuilder);
            if (it.hasNext()) {
                queryBuilder.append(", ");
            }
        }
    }
}
