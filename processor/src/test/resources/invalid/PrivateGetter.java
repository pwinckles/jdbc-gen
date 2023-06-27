package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

@JdbcGen
@JdbcGenTable(name = " test")
public class PrivateGetter {

    @JdbcGenColumn(name = "id", identity = true)
    private Long id;

    @JdbcGenColumn(name = "val")
    private String value;

    private Long getId() {
        return id;
    }

    public PrivateGetter setId(Long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PrivateGetter setValue(String value) {
        this.value = value;
        return this;
    }
}
