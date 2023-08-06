package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.test.ExampleEnum;
import java.time.Instant;

public class Example {

    private Long id;
    private String name;
    private long count;
    private Instant timestamp;
    private ExampleEnum exampleEnum;

    public Long getId() {
        return id;
    }

    public Example setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Example setName(String name) {
        this.name = name;
        return this;
    }

    public long getCount() {
        return count;
    }

    public Example setCount(long count) {
        this.count = count;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Example setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ExampleEnum getExampleEnum() {
        return exampleEnum;
    }

    public Example setExampleEnum(ExampleEnum exampleEnum) {
        this.exampleEnum = exampleEnum;
        return this;
    }

    @Override
    public String toString() {
        return "Example{" + "id="
                + id + ", name='"
                + name + '\'' + ", count="
                + count + ", timestamp="
                + timestamp + ", exampleEnum="
                + exampleEnum + '}';
    }
}
