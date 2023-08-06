package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Represents a grouping of filter predicates. This is represented in SQL with parenthesis.
 */
public class Group implements FilterPart {

    private final boolean negate;
    private final List<FilterPart> filterParts;

    /**
     * Creates a new group that contains the predicates in the specified filter.
     *
     * @param filter the filter to insert into the group
     * @return the group
     */
    public static Group group(Filter filter) {
        return new Group(filter, false);
    }

    /**
     * Creates a new negated group that contains the predicates in the specified filter. This is represented in SQL
     * as "NOT (...)".
     *
     * @param filter the filter to insert in the group
     * @return the group
     */
    public static Group notGroup(Filter filter) {
        return new Group(filter, true);
    }

    private Group(Filter filter, boolean negate) {
        Objects.requireNonNull(filter, "filter cannot be null");
        this.filterParts = List.copyOf(filter.getFilterParts());
        this.negate = negate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildQuery(StringBuilder queryBuilder) {
        if (filterParts.isEmpty()) {
            return;
        }
        if (negate) {
            queryBuilder.append("NOT ");
        }
        queryBuilder.append("(");
        filterParts.forEach(part -> part.buildQuery(queryBuilder));
        queryBuilder.append(")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        var position = currentPosition;
        for (var part : filterParts) {
            position = part.addArguments(position, statement);
        }
        return position;
    }
}
