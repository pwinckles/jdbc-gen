package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

// TODO javadoc
public class ConjunctionBuilder<B> {

    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    // TODO javadoc
    public ConjunctionBuilder(Filter filter, FilterBuilderHelper<B> helper) {
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.helper = Objects.requireNonNull(helper, "helper cannot be null");
    }

    // TODO javadoc
    public B and() {
        filter.add(Conjunction.AND);
        return helper.filterBuilder();
    }

    // TODO javadoc
    public B or() {
        filter.add(Conjunction.OR);
        return helper.filterBuilder();
    }
}
