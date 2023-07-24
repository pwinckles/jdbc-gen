package com.pwinckles.jdbcgen.filter;

import java.util.Objects;
import java.util.function.Function;

// TODO javadoc
public class ConjunctionBuilder<B> {

    private final Filter filter;
    private final Function<Filter, B> newBuilder;

    // TODO javadoc
    public ConjunctionBuilder(Filter filter, Function<Filter, B> newBuilder) {
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.newBuilder = Objects.requireNonNull(newBuilder, "newBuilder cannot be null");
    }

    // TODO javadoc
    public B and() {
        filter.add(Conjunction.AND);
        return newBuilder.apply(filter);
    }

    // TODO javadoc
    public B or() {
        filter.add(Conjunction.OR);
        return newBuilder.apply(filter);
    }
}
