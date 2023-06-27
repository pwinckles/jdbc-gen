package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

@JdbcGen
@JdbcGenTable(name = " test")
public class PrivateSetter {

    @JdbcGenColumn(name = "id", identity = true)
    private Long id;

    @JdbcGenColumn(name = "val")
    private String value;

    public Long getId() {
        return id;
    }

    private PrivateSetter setId(Long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PrivateSetter setValue(String value) {
        this.value = value;
        return this;
    }
}
