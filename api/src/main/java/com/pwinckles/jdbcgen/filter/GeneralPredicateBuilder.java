package com.pwinckles.jdbcgen.filter;

import java.util.Objects;
import java.util.function.Function;

// TODO javadoc
public class GeneralPredicateBuilder<B, T> {

    private final String field;
    private final Filter filter;
    private final Function<Filter, B> newBuilder;

    // TODO javadoc
    public GeneralPredicateBuilder(String field, Filter filter, Function<Filter, B> newBuilder) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.newBuilder = Objects.requireNonNull(newBuilder, "newBuilder cannot be null");
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isEqualTo(T value) {
        filter.add(new Predicate(field, Operation.EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotEqualTo(T value) {
        filter.add(new Predicate(field, Operation.NOT_EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThan(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.GREATER_THAN, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThan(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LESS_THAN, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThanOrEqualTo(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNull() {
        return isEqualTo(null);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotNull() {
        return isNotEqualTo(null);
    }
}
