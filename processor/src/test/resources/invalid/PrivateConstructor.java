package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

@JdbcGen
@JdbcGenTable(name = " test")
public class PrivateConstructor {

    @JdbcGenColumn(name = "id", identity = true)
    private Long id;

    @JdbcGenColumn(name = "val")
    private String value;

    private PrivateConstructor() {

    }

    public Long getId() {
        return id;
    }

    public PrivateConstructor setId(Long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PrivateConstructor setValue(String value) {
        this.value = value;
        return this;
    }
}
