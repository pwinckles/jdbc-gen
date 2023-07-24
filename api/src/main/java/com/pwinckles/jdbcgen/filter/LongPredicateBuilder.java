package com.pwinckles.jdbcgen.filter;

import java.util.Objects;
import java.util.function.Function;

// TODO javadoc
public class LongPredicateBuilder<B> {

    // TODO add the other primitives

    private final String field;
    private final Filter filter;
    private final Function<Filter, B> newBuilder;

    // TODO javadoc
    public LongPredicateBuilder(String field, Filter filter, Function<Filter, B> newBuilder) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.newBuilder = Objects.requireNonNull(newBuilder, "newBuilder cannot be null");
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.NOT_EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThan(long value) {
        filter.add(new LongPredicate(field, Operation.GREATER_THAN, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThan(long value) {
        filter.add(new LongPredicate(field, Operation.LESS_THAN, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThanOrEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }
}
