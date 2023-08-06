package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Part of a filter that can be transformed into SQL and add positional arguments as appropriate.
 */
public interface FilterPart {

    /**
     * Appends a SQL representation of the filter part to the queryBuilder
     *
     * @param queryBuilder the builder that is constructing the SQL query
     */
    void buildQuery(StringBuilder queryBuilder);

    /**
     * Sets positional arguments on the statement as appropriate, and returns the index of the next argument. If there
     * are no arguments to add, the currentPosition should be returned.
     *
     * @param currentPosition the next available position to insert an argument at
     * @param statement the statement to add arguments to
     * @return the next available position to add another argument at
     * @throws SQLException
     */
    default int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        return currentPosition;
    }
}
