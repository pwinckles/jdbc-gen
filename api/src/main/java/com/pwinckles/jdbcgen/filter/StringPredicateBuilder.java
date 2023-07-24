package com.pwinckles.jdbcgen.filter;

import java.util.Objects;
import java.util.function.Function;

// TODO javadoc
public class StringPredicateBuilder<B> extends GeneralPredicateBuilder<B, String> {

    private final String field;
    private final Filter filter;
    private final Function<Filter, B> newBuilder;

    // TODO javadoc
    public StringPredicateBuilder(String field, Filter filter, Function<Filter, B> newBuilder) {
        super(field, filter, newBuilder);
        this.field = field;
        this.filter = filter;
        this.newBuilder = newBuilder;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLike(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LIKE, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotLike(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.NOT_LIKE, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLikeInsensitive(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LIKE_INSENSITIVE, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotLikeInsensitive(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.NOT_LIKE_INSENSITIVE, value));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }
}
