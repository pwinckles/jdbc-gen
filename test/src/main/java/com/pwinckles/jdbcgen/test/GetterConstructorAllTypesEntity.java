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
public class GetterConstructorAllTypesEntity {

    @JdbcGenColumn(name = "at_id", identity = true)
    private final Long longId;

    @JdbcGenColumn(name = "at_long_prim")
    private final long longPrim;

    @JdbcGenColumn(name = "at_int_obj")
    private final Integer intObj;

    @JdbcGenColumn(name = "at_int_prim")
    private final int intPrim;

    @JdbcGenColumn(name = "at_short_obj")
    private final Short shortObj;

    @JdbcGenColumn(name = "at_short_prim")
    private final short shortPrim;

    @JdbcGenColumn(name = "at_double_obj")
    private final Double doubleObj;

    @JdbcGenColumn(name = "at_double_prim")
    private final double doublePrim;

    @JdbcGenColumn(name = "at_bool_obj")
    private final Boolean boolObj;

    @JdbcGenColumn(name = "at_bool_prim")
    private final boolean boolPrim;

    @JdbcGenColumn(name = "at_string")
    private final String string;

    @JdbcGenColumn(name = "at_instant")
    private final Instant instant;

    @JdbcGenColumn(name = "at_local_date_time")
    private final LocalDateTime localDateTime;

    @JdbcGenColumn(name = "at_local_date")
    private final LocalDate localDate;

    @JdbcGenColumn(name = "at_offset_date_time")
    private final OffsetDateTime offsetDateTime;

    @JdbcGenColumn(name = "at_date")
    private final Date date;

    @JdbcGenColumn(name = "at_timestamp")
    private final Timestamp timestamp;

    @JdbcGenColumn(name = "at_byte_array")
    private final byte[] byteArray;

    @JdbcGenColumn(name = "at_uuid")
    private final UUID uuid;

    public GetterConstructorAllTypesEntity(Long longId,
                                           long longPrim,
                                           Integer intObj,
                                           int intPrim,
                                           Short shortObj,
                                           short shortPrim,
                                           Double doubleObj,
                                           double doublePrim,
                                           Boolean boolObj,
                                           boolean boolPrim,
                                           String string,
                                           Instant instant,
                                           LocalDateTime localDateTime,
                                           LocalDate localDate,
                                           OffsetDateTime offsetDateTime,
                                           Date date,
                                           Timestamp timestamp,
                                           byte[] byteArray,
                                           UUID uuid) {
        this.longId = longId;
        this.longPrim = longPrim;
        this.intObj = intObj;
        this.intPrim = intPrim;
        this.shortObj = shortObj;
        this.shortPrim = shortPrim;
        this.doubleObj = doubleObj;
        this.doublePrim = doublePrim;
        this.boolObj = boolObj;
        this.boolPrim = boolPrim;
        this.string = string;
        this.instant = instant;
        this.localDateTime = localDateTime;
        this.localDate = localDate;
        this.offsetDateTime = offsetDateTime;
        this.date = date;
        this.timestamp = timestamp;
        this.byteArray = byteArray;
        this.uuid = uuid;
    }

    public Long getLongId() {
        return longId;
    }

    public long getLongPrim() {
        return longPrim;
    }

    public Integer getIntObj() {
        return intObj;
    }

    public int getIntPrim() {
        return intPrim;
    }

    public Short getShortObj() {
        return shortObj;
    }

    public short getShortPrim() {
        return shortPrim;
    }

    public Double getDoubleObj() {
        return doubleObj;
    }

    public double getDoublePrim() {
        return doublePrim;
    }

    public Boolean getBoolObj() {
        return boolObj;
    }

    public boolean isBoolPrim() {
        return boolPrim;
    }

    public String getString() {
        return string;
    }

    public Instant getInstant() {
        return instant;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public Date getDate() {
        return date;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public UUID getUuid() {
        return uuid;
    }

}
