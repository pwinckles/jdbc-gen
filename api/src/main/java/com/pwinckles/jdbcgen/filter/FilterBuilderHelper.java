package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

/**
 * Helper class to make it easy for predicate builders to chain back to the conjunction builder or main filter builder.
 *
 * @param <B> the entity's filter builder type
 */
public class FilterBuilderHelper<B> {

    private final B filterBuilder;
    private final ConjunctionBuilder<B> conjunctionBuilder;

    /**
     * @param filter the filter that's being constructed
     * @param filterBuilder the entity's filter builder
     */
    public FilterBuilderHelper(Filter filter, B filterBuilder) {
        Objects.requireNonNull(filter, "filter cannot be null");
        this.filterBuilder = Objects.requireNonNull(filterBuilder, "filterBuilder cannot be null");
        this.conjunctionBuilder = new ConjunctionBuilder<>(filter, this);
    }

    /**
     * @return the entity's filter builder
     */
    public B filterBuilder() {
        return filterBuilder;
    }

    /**
     * Returns the conjunction builder, allowing a new predicate to be added to the filter.
     *
     * @return the conjunction builder
     */
    public ConjunctionBuilder<B> conjunctionBuilder() {
        return conjunctionBuilder;
    }
}
