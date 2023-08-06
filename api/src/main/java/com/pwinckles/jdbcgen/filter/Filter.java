package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SQL WHERE clause.
 */
public class Filter {

    private final List<FilterPart> filterParts = new ArrayList<>();

    /**
     * Add a part to the filter
     *
     * @param filterPart the part to add
     */
    public void add(FilterPart filterPart) {
        filterParts.add(filterPart);
    }

    /**
     * Transforms the filter into a SQL query and appends it to the query builder. If the filter is empty, nothing
     * is appended.
     *
     * @param queryBuilder the query builder to append to
     */
    public void buildQuery(StringBuilder queryBuilder) {
        if (filterParts.isEmpty()) {
            return;
        }
        queryBuilder.append(" WHERE ");
        filterParts.forEach(part -> part.buildQuery(queryBuilder));
    }

    /**
     * Adds any values that the filter is matching against to the prepared statement starting at 'currentPosition',
     * and returns the new current position.
     *
     * @param currentPosition the index to start inserting arguments at
     * @param statement the statement to add filter arguments to
     * @return the new index
     * @throws SQLException
     */
    public int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        var position = currentPosition;
        for (var part : filterParts) {
            position = part.addArguments(position, statement);
        }
        return position;
    }

    List<FilterPart> getFilterParts() {
        return List.copyOf(filterParts);
    }
}
