package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.filter.BaseFilterBuilder;
import com.pwinckles.jdbcgen.filter.Filter;
import com.pwinckles.jdbcgen.filter.GeneralPredicateBuilder;
import com.pwinckles.jdbcgen.filter.LongPredicateBuilder;
import com.pwinckles.jdbcgen.filter.StringPredicateBuilder;
import java.time.Instant;

public class ExampleFilterBuilder extends BaseFilterBuilder<ExampleFilterBuilder> {

    private final Filter filter;

    public ExampleFilterBuilder(Filter filter) {
        super(filter, ExampleFilterBuilder::new);
        this.filter = filter;
    }

    public GeneralPredicateBuilder<ExampleFilterBuilder, Long> id() {
        return new GeneralPredicateBuilder<>("id", filter, ExampleFilterBuilder::new);
    }

    public StringPredicateBuilder<ExampleFilterBuilder> name() {
        return new StringPredicateBuilder<>("name", filter, ExampleFilterBuilder::new);
    }

    public LongPredicateBuilder<ExampleFilterBuilder> count() {
        return new LongPredicateBuilder<>("count", filter, ExampleFilterBuilder::new);
    }

    public GeneralPredicateBuilder<ExampleFilterBuilder, Instant> timestamp() {
        return new GeneralPredicateBuilder<>("timestamp", filter, ExampleFilterBuilder::new);
    }
}
