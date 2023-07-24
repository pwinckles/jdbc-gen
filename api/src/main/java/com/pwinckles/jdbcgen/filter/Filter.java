package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// TODO javadoc
public class Filter {

    private final List<FilterPart> filterParts = new ArrayList<>();

    // TODO javadoc
    public void add(FilterPart filterPart) {
        filterParts.add(filterPart);
    }

    // TODO javadoc
    public void buildQuery(StringBuilder queryBuilder) {
        if (filterParts.isEmpty()) {
            return;
        }
        queryBuilder.append(" WHERE ");
        filterParts.forEach(part -> part.buildQuery(queryBuilder));
    }

    // TODO javadoc
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
