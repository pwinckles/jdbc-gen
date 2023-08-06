package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@JdbcGen
@JdbcGenTable(name = "all_types")
public class DirectAllTypesEntity implements Cloneable {

    @JdbcGenColumn(name = "at_id", identity = true)
    public Long longId;

    @JdbcGenColumn(name = "at_long_prim")
    public long longPrim;

    @JdbcGenColumn(name = "at_int_obj")
    public Integer intObj;

    @JdbcGenColumn(name = "at_int_prim")
    public int intPrim;

    @JdbcGenColumn(name = "at_short_obj")
    public Short shortObj;

    @JdbcGenColumn(name = "at_short_prim")
    public short shortPrim;

    @JdbcGenColumn(name = "at_double_obj")
    public Double doubleObj;

    @JdbcGenColumn(name = "at_double_prim")
    public double doublePrim;

    @JdbcGenColumn(name = "at_bool_obj")
    public Boolean boolObj;

    @JdbcGenColumn(name = "at_bool_prim")
    public boolean boolPrim;

    @JdbcGenColumn(name = "at_string")
    public String string;

    @JdbcGenColumn(name = "at_instant")
    public Instant instant;

    @JdbcGenColumn(name = "at_local_date_time")
    public LocalDateTime localDateTime;

    @JdbcGenColumn(name = "at_local_date")
    public LocalDate localDate;

    @JdbcGenColumn(name = "at_offset_date_time")
    public OffsetDateTime offsetDateTime;

    @JdbcGenColumn(name = "at_date")
    public Date date;

    @JdbcGenColumn(name = "at_timestamp")
    public Timestamp timestamp;

    @JdbcGenColumn(name = "at_byte_array")
    public byte[] byteArray;

    @JdbcGenColumn(name = "at_uuid")
    public UUID uuid;

    @JdbcGenColumn(name = "at_enum")
    public ExampleEnum exampleEnum;

    @Override
    public DirectAllTypesEntity clone() {
        var clone = new DirectAllTypesEntity();
        clone.longId = longId;
        clone.longPrim = longPrim;
        clone.intObj = intObj;
        clone.intPrim = intPrim;
        clone.shortObj = shortObj;
        clone.shortPrim = shortPrim;
        clone.doubleObj = doubleObj;
        clone.doublePrim = doublePrim;
        clone.boolObj = boolObj;
        clone.boolPrim = boolPrim;
        clone.string = string;
        clone.instant = instant;
        clone.localDateTime = localDateTime;
        clone.localDate = localDate;
        clone.offsetDateTime = offsetDateTime;
        clone.date = date;
        clone.timestamp = timestamp;
        clone.byteArray = byteArray;
        clone.uuid = uuid;
        clone.exampleEnum = exampleEnum;
        return clone;
    }
}
