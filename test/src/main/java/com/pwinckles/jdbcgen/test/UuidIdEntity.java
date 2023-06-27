package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

import java.util.UUID;

@JdbcGen
@JdbcGenTable(name = "uuid_id")
public class UuidIdEntity implements Cloneable{

    @JdbcGenColumn(name = "id", identity = true)
    private UUID id;

    @JdbcGenColumn(name = "val")
    private String value;

    public UUID getId() {
        return id;
    }

    public UuidIdEntity setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public UuidIdEntity setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public UuidIdEntity clone() {
        return new UuidIdEntity().setId(id).setValue(value);
    }
}
