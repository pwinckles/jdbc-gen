package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// TODO javadoc
interface FilterPart {

    // TODO javadoc
    void buildQuery(StringBuilder queryBuilder);

    // TODO javadoc
    default int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        return currentPosition;
    }
}
