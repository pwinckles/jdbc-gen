package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

// TODO javadoc
public class FilterBuilderHelper<B> {

    private final B filterBuilder;
    private final ConjunctionBuilder<B> conjunctionBuilder;

    // TODO javadoc
    public FilterBuilderHelper(Filter filter, B filterBuilder) {
        Objects.requireNonNull(filter, "filter cannot be null");
        this.filterBuilder = Objects.requireNonNull(filterBuilder, "filterBuilder cannot be null");
        this.conjunctionBuilder = new ConjunctionBuilder<>(filter, this);
    }

    // TODO javadoc
    public B filterBuilder() {
        return filterBuilder;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> conjunctionBuilder() {
        return conjunctionBuilder;
    }
}
