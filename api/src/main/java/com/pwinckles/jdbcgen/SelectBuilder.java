package com.pwinckles.jdbcgen;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Constructs a select query. By default, selects will select all entities with no defined order.
 *
 * @param <F> the filter builder type
 * @param <S> the sort builder type
 */
public final class SelectBuilder<F, S> {

    private final F filterBuilder;
    private final S sortBuilder;

    public SelectBuilder(F filterBuilder, S sortBuilder) {
        this.filterBuilder = Objects.requireNonNull(filterBuilder, "filterBuilder cannot be null");
        this.sortBuilder = Objects.requireNonNull(sortBuilder, "sortBuilder cannot be null");
    }

    /**
     * Adds a filter to the select.
     *
     * @param filterBuilder the filter builder
     * @return select builder
     */
    public SelectBuilder<F, S> filter(Consumer<F> filterBuilder) {
        filterBuilder.accept(this.filterBuilder);
        return this;
    }

    /**
     * Adds a sort order to the select.
     *
     * @param sortBuilder the sort builder
     * @return select builder
     */
    public SelectBuilder<F, S> sort(Consumer<S> sortBuilder) {
        sortBuilder.accept(this.sortBuilder);
        return this;
    }
}
