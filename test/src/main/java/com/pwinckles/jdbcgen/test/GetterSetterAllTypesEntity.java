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
public class GetterSetterAllTypesEntity implements Cloneable {

    @JdbcGenColumn(name = "at_id", identity = true)
    private Long longId;

    @JdbcGenColumn(name = "at_long_prim")
    private long longPrim;

    @JdbcGenColumn(name = "at_int_obj")
    private Integer intObj;

    @JdbcGenColumn(name = "at_int_prim")
    private int intPrim;

    @JdbcGenColumn(name = "at_short_obj")
    private Short shortObj;

    @JdbcGenColumn(name = "at_short_prim")
    private short shortPrim;

    @JdbcGenColumn(name = "at_double_obj")
    private Double doubleObj;

    @JdbcGenColumn(name = "at_double_prim")
    private double doublePrim;

    @JdbcGenColumn(name = "at_bool_obj")
    private Boolean boolObj;

    @JdbcGenColumn(name = "at_bool_prim")
    private boolean boolPrim;

    @JdbcGenColumn(name = "at_string")
    private String string;

    @JdbcGenColumn(name = "at_instant")
    private Instant instant;

    @JdbcGenColumn(name = "at_local_date_time")
    private LocalDateTime localDateTime;

    @JdbcGenColumn(name = "at_local_date")
    private LocalDate localDate;

    @JdbcGenColumn(name = "at_offset_date_time")
    private OffsetDateTime offsetDateTime;

    @JdbcGenColumn(name = "at_date")
    private Date date;

    @JdbcGenColumn(name = "at_timestamp")
    private Timestamp timestamp;

    @JdbcGenColumn(name = "at_byte_array")
    private byte[] byteArray;

    @JdbcGenColumn(name = "at_uuid")
    private UUID uuid;

    public Long getLongId() {
        return longId;
    }

    public GetterSetterAllTypesEntity setLongId(Long longId) {
        this.longId = longId;
        return this;
    }

    public long getLongPrim() {
        return longPrim;
    }

    public GetterSetterAllTypesEntity setLongPrim(long longPrim) {
        this.longPrim = longPrim;
        return this;
    }

    public Integer getIntObj() {
        return intObj;
    }

    public GetterSetterAllTypesEntity setIntObj(Integer intObj) {
        this.intObj = intObj;
        return this;
    }

    public int getIntPrim() {
        return intPrim;
    }

    public GetterSetterAllTypesEntity setIntPrim(int intPrim) {
        this.intPrim = intPrim;
        return this;
    }

    public Short getShortObj() {
        return shortObj;
    }

    public GetterSetterAllTypesEntity setShortObj(Short shortObj) {
        this.shortObj = shortObj;
        return this;
    }

    public short getShortPrim() {
        return shortPrim;
    }

    public GetterSetterAllTypesEntity setShortPrim(short shortPrim) {
        this.shortPrim = shortPrim;
        return this;
    }

    public Double getDoubleObj() {
        return doubleObj;
    }

    public GetterSetterAllTypesEntity setDoubleObj(Double doubleObj) {
        this.doubleObj = doubleObj;
        return this;
    }

    public double getDoublePrim() {
        return doublePrim;
    }

    public GetterSetterAllTypesEntity setDoublePrim(double doublePrim) {
        this.doublePrim = doublePrim;
        return this;
    }

    public Boolean getBoolObj() {
        return boolObj;
    }

    public GetterSetterAllTypesEntity setBoolObj(Boolean boolObj) {
        this.boolObj = boolObj;
        return this;
    }

    public boolean isBoolPrim() {
        return boolPrim;
    }

    public GetterSetterAllTypesEntity setBoolPrim(boolean boolPrim) {
        this.boolPrim = boolPrim;
        return this;
    }

    public String getString() {
        return string;
    }

    public GetterSetterAllTypesEntity setString(String string) {
        this.string = string;
        return this;
    }

    public Instant getInstant() {
        return instant;
    }

    public GetterSetterAllTypesEntity setInstant(Instant instant) {
        this.instant = instant;
        return this;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public GetterSetterAllTypesEntity setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public GetterSetterAllTypesEntity setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
        return this;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public GetterSetterAllTypesEntity setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public GetterSetterAllTypesEntity setDate(Date date) {
        this.date = date;
        return this;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public GetterSetterAllTypesEntity setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public GetterSetterAllTypesEntity setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GetterSetterAllTypesEntity setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public GetterSetterAllTypesEntity clone() {
        var clone = new GetterSetterAllTypesEntity();
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
        return clone;
    }
}
