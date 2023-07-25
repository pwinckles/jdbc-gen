package com.pwinckles.jdbcgen.filter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

// TODO javadoc
public class CollectionPredicateBuilder<B, T> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    // TODO javadoc
    public CollectionPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isIn(Collection<T> values) {
        Objects.requireNonNull(values, "values cannot be null");
        filter.add(InListPredicate.inList(field, List.copyOf(values)));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotIn(Collection<T> values) {
        Objects.requireNonNull(values, "values cannot be null");
        filter.add(InListPredicate.notInList(field, List.copyOf(values)));
        return helper.conjunctionBuilder();
    }
}
