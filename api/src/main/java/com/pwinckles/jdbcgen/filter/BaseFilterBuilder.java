package com.pwinckles.jdbcgen.filter;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

// TODO javadoc
public abstract class BaseFilterBuilder<B> {

    private final Filter filter;
    private final Function<Filter, B> newBuilder;

    // TODO javadoc
    protected BaseFilterBuilder(Filter filter, Function<Filter, B> newBuilder) {
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.newBuilder = Objects.requireNonNull(newBuilder, "newBuilder cannot be null");
    }

    // TODO javadoc
    public ConjunctionBuilder<B> group(Consumer<B> filterBuilder) {
        var groupFilter = new Filter();
        filterBuilder.accept(newBuilder.apply(groupFilter));
        filter.add(Group.group(groupFilter));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> notGroup(Consumer<B> filterBuilder) {
        var groupFilter = new Filter();
        filterBuilder.accept(newBuilder.apply(groupFilter));
        filter.add(Group.notGroup(groupFilter));
        return new ConjunctionBuilder<>(filter, newBuilder);
    }
}
