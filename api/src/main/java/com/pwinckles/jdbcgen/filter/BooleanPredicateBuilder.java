package com.pwinckles.jdbcgen.filter;

import java.util.Objects;
import java.util.function.Function;

// TODO javadoc
public class BooleanPredicateBuilder<B> {

    private final String field;
    private final Filter filter;
    private final Function<Filter, B> newBuilder;

    // TODO javadoc
    public BooleanPredicateBuilder(String field, Filter filter, Function<Filter, B> newBuilder) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.newBuilder = Objects.requireNonNull(newBuilder, "newBuilder cannot be null");
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isTrue() {
        filter.add(new Predicate(field, Operation.EQUAL, true));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isFalse() {
        filter.add(new Predicate(field, Operation.EQUAL, false));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNull() {
        filter.add(new Predicate(field, Operation.EQUAL, null));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }
}
