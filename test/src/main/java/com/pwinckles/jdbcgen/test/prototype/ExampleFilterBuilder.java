package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.filter.ConjunctionBuilder;
import com.pwinckles.jdbcgen.filter.EnumPredicateBuilder;
import com.pwinckles.jdbcgen.filter.Filter;
import com.pwinckles.jdbcgen.filter.FilterBuilderHelper;
import com.pwinckles.jdbcgen.filter.Group;
import com.pwinckles.jdbcgen.filter.LongPredicateBuilder;
import com.pwinckles.jdbcgen.filter.ObjectPredicateBuilder;
import com.pwinckles.jdbcgen.filter.StringPredicateBuilder;
import com.pwinckles.jdbcgen.test.ExampleEnum;
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

    public ObjectPredicateBuilder<ExampleFilterBuilder, Long> id() {
        return new ObjectPredicateBuilder<>("id", filter, helper);
    }

    public StringPredicateBuilder<ExampleFilterBuilder> name() {
        return new StringPredicateBuilder<>("name", filter, helper);
    }

    public LongPredicateBuilder<ExampleFilterBuilder> count() {
        return new LongPredicateBuilder<>("count", filter, helper);
    }

    public ObjectPredicateBuilder<ExampleFilterBuilder, Instant> timestamp() {
        return new ObjectPredicateBuilder<>("timestamp", filter, helper);
    }

    public EnumPredicateBuilder<ExampleFilterBuilder, ExampleEnum> exampleEnum() {
        return new EnumPredicateBuilder<>("enum", filter, helper);
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
