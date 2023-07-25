package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

// TODO javadoc
class InListPredicate implements FilterPart {

    private final String field;
    private final List<Object> values;
    private final boolean negate;

    // TODO javadoc
    public static InListPredicate inList(String field, List<Object> values) {
        return new InListPredicate(field, values, false);
    }

    // TODO javadoc
    public static InListPredicate notInList(String field, List<Object> values) {
        return new InListPredicate(field, values, true);
    }

    // TODO javadoc
    private InListPredicate(String field, List<Object> values, boolean negate) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.values = Objects.requireNonNull(values, "values cannot be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must contain at least one entry");
        }
        this.negate = negate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildQuery(StringBuilder queryBuilder) {
        queryBuilder.append(field).append(" ");

        if (negate) {
            queryBuilder.append("NOT ");
        }

        queryBuilder.append("IN (");

        queryBuilder.append("?, ".repeat(values.size()));
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());

        queryBuilder.append(")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        for (int i = 0; i < values.size(); i++) {
            statement.setObject(currentPosition + i, values.get(i));
        }
        return currentPosition + values.size();
    }
}
