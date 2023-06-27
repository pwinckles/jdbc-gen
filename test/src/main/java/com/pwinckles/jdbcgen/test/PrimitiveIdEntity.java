package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

@JdbcGen
@JdbcGenTable(name = "primitive_id")
public class PrimitiveIdEntity implements Cloneable{

    @JdbcGenColumn(name = "id", identity = true)
    private long id;

    @JdbcGenColumn(name = "val")
    private String value;

    public long getId() {
        return id;
    }

    public PrimitiveIdEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PrimitiveIdEntity setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public PrimitiveIdEntity clone() {
        return new PrimitiveIdEntity().setId(id).setValue(value);
    }
}
