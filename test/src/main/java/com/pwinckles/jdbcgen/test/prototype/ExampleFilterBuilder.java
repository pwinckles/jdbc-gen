package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.filter.ConjunctionBuilder;
import com.pwinckles.jdbcgen.filter.Filter;
import com.pwinckles.jdbcgen.filter.FilterBuilderHelper;
import com.pwinckles.jdbcgen.filter.GeneralPredicateBuilder;
import com.pwinckles.jdbcgen.filter.Group;
import com.pwinckles.jdbcgen.filter.LongPredicateBuilder;
import com.pwinckles.jdbcgen.filter.StringPredicateBuilder;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

public class ExampleFilterBuilder {

    private final Filter filter;
    private final FilterBuilderHelper<ExampleFilterBuilder> helper;

    public ExampleFilterBuilder(Filter filter) {
        this.filter = Objects.requireNonNull(filter, "filter cannot be null");
        this.helper = new FilterBuilderHelper<>(filter, this);
    }

    public GeneralPredicateBuilder<ExampleFilterBuilder, Long> id() {
        return new GeneralPredicateBuilder<>("id", filter, helper);
    }

    public StringPredicateBuilder<ExampleFilterBuilder> name() {
        return new StringPredicateBuilder<>("name", filter, helper);
    }

    public LongPredicateBuilder<ExampleFilterBuilder> count() {
        return new LongPredicateBuilder<>("count", filter, helper);
    }

    public GeneralPredicateBuilder<ExampleFilterBuilder, Instant> timestamp() {
        return new GeneralPredicateBuilder<>("timestamp", filter, helper);
    }

    public ConjunctionBuilder<ExampleFilterBuilder> group(Consumer<ExampleFilterBuilder> filterBuilder) {
        var groupFilter = new Filter();
        filterBuilder.accept(new ExampleFilterBuilder(groupFilter));
        filter.add(Group.group(groupFilter));
        return helper.conjunctionBuilder();
    }

    public ConjunctionBuilder<ExampleFilterBuilder> notGroup(Consumer<ExampleFilterBuilder> filterBuilder) {
        var groupFilter = new Filter();
        filterBuilder.accept(new ExampleFilterBuilder(groupFilter));
        filter.add(Group.notGroup(groupFilter));
        return helper.conjunctionBuilder();
    }
}
