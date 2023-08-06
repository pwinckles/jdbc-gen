package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.sort.SortBuilder;
import java.util.Objects;

public class ExampleSortBuilder {

    private final SortBuilder sortBuilder;

    public ExampleSortBuilder(SortBuilder sortBuilder) {
        this.sortBuilder = Objects.requireNonNull(sortBuilder, "sortBuilder cannot be null");
    }

    public ExampleSortBuilder idAsc() {
        sortBuilder.asc("id");
        return this;
    }

    public ExampleSortBuilder idDesc() {
        sortBuilder.desc("id");
        return this;
    }

    public ExampleSortBuilder nameAsc() {
        sortBuilder.asc("name");
        return this;
    }

    public ExampleSortBuilder nameDesc() {
        sortBuilder.desc("name");
        return this;
    }

    public ExampleSortBuilder countAsc() {
        sortBuilder.asc("count");
        return this;
    }

    public ExampleSortBuilder countDesc() {
        sortBuilder.desc("count");
        return this;
    }

    public ExampleSortBuilder timestampAsc() {
        sortBuilder.asc("timestamp");
        return this;
    }

    public ExampleSortBuilder timestampDesc() {
        sortBuilder.desc("timestamp");
        return this;
    }
}
