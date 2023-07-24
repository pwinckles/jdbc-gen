package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

// TODO javadoc
public class BooleanPredicateBuilder<B> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    // TODO javadoc
    public BooleanPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.helper = Objects.requireNonNull(helper, "helper cannot be null");
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isTrue() {
        filter.add(new Predicate(field, Operation.EQUAL, true));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isFalse() {
        filter.add(new Predicate(field, Operation.EQUAL, false));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNull() {
        filter.add(new Predicate(field, Operation.EQUAL, null));
        return helper.conjunctionBuilder();
    }
}
