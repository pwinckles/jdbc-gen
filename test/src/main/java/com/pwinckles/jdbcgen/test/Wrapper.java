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

class Wrapper {

    @JdbcGen(name = "DirectAllTypesEntityInnerDb")
    @JdbcGenTable(name = "all_types")
    static class DirectAllTypesEntity implements Cloneable {

        @JdbcGenColumn(name = "at_id", identity = true)
        Long longId;

        @JdbcGenColumn(name = "at_long_prim")
        long longPrim;

        @JdbcGenColumn(name = "at_int_obj")
        Integer intObj;

        @JdbcGenColumn(name = "at_int_prim")
        int intPrim;

        @JdbcGenColumn(name = "at_short_obj")
        Short shortObj;

        @JdbcGenColumn(name = "at_short_prim")
        short shortPrim;

        @JdbcGenColumn(name = "at_double_obj")
        Double doubleObj;

        @JdbcGenColumn(name = "at_double_prim")
        double doublePrim;

        @JdbcGenColumn(name = "at_bool_obj")
        Boolean boolObj;

        @JdbcGenColumn(name = "at_bool_prim")
        boolean boolPrim;

        @JdbcGenColumn(name = "at_string")
        String string;

        @JdbcGenColumn(name = "at_instant")
        Instant instant;

        @JdbcGenColumn(name = "at_local_date_time")
        LocalDateTime localDateTime;

        @JdbcGenColumn(name = "at_local_date")
        LocalDate localDate;

        @JdbcGenColumn(name = "at_offset_date_time")
        OffsetDateTime offsetDateTime;

        @JdbcGenColumn(name = "at_date")
        Date date;

        @JdbcGenColumn(name = "at_timestamp")
        Timestamp timestamp;

        @JdbcGenColumn(name = "at_byte_array")
        byte[] byteArray;

        @JdbcGenColumn(name = "at_uuid")
        UUID uuid;

        @JdbcGenColumn(name = "at_enum")
        ExampleEnum exampleEnum;

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

    @JdbcGen(name = "GetterConstructorAllTypesEntityInnerDb")
    @JdbcGenTable(name = "all_types")
    static class GetterConstructorAllTypesEntity {

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

        @JdbcGenColumn(name = "at_enum")
        private ExampleEnum exampleEnum;

        GetterConstructorAllTypesEntity(
                Long longId,
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
                UUID uuid,
                ExampleEnum exampleEnum) {
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
            this.exampleEnum = exampleEnum;
        }

        Long getLongId() {
            return longId;
        }

        long getLongPrim() {
            return longPrim;
        }

        Integer getIntObj() {
            return intObj;
        }

        int getIntPrim() {
            return intPrim;
        }

        Short getShortObj() {
            return shortObj;
        }

        short getShortPrim() {
            return shortPrim;
        }

        Double getDoubleObj() {
            return doubleObj;
        }

        double getDoublePrim() {
            return doublePrim;
        }

        Boolean getBoolObj() {
            return boolObj;
        }

        boolean isBoolPrim() {
            return boolPrim;
        }

        String getString() {
            return string;
        }

        Instant getInstant() {
            return instant;
        }

        LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        LocalDate getLocalDate() {
            return localDate;
        }

        OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        Date getDate() {
            return date;
        }

        Timestamp getTimestamp() {
            return timestamp;
        }

        byte[] getByteArray() {
            return byteArray;
        }

        UUID getUuid() {
            return uuid;
        }

        public ExampleEnum getExampleEnum() {
            return exampleEnum;
        }
    }

    @JdbcGen(name = "GetterSetterAllTypesEntityInnerDb")
    @JdbcGenTable(name = "all_types")
    static class GetterSetterAllTypesEntity implements Cloneable {

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

        @JdbcGenColumn(name = "at_enum")
        private ExampleEnum exampleEnum;

        Long getLongId() {
            return longId;
        }

        GetterSetterAllTypesEntity setLongId(Long longId) {
            this.longId = longId;
            return this;
        }

        long getLongPrim() {
            return longPrim;
        }

        GetterSetterAllTypesEntity setLongPrim(long longPrim) {
            this.longPrim = longPrim;
            return this;
        }

        Integer getIntObj() {
            return intObj;
        }

        GetterSetterAllTypesEntity setIntObj(Integer intObj) {
            this.intObj = intObj;
            return this;
        }

        int getIntPrim() {
            return intPrim;
        }

        GetterSetterAllTypesEntity setIntPrim(int intPrim) {
            this.intPrim = intPrim;
            return this;
        }

        Short getShortObj() {
            return shortObj;
        }

        GetterSetterAllTypesEntity setShortObj(Short shortObj) {
            this.shortObj = shortObj;
            return this;
        }

        short getShortPrim() {
            return shortPrim;
        }

        GetterSetterAllTypesEntity setShortPrim(short shortPrim) {
            this.shortPrim = shortPrim;
            return this;
        }

        Double getDoubleObj() {
            return doubleObj;
        }

        GetterSetterAllTypesEntity setDoubleObj(Double doubleObj) {
            this.doubleObj = doubleObj;
            return this;
        }

        double getDoublePrim() {
            return doublePrim;
        }

        GetterSetterAllTypesEntity setDoublePrim(double doublePrim) {
            this.doublePrim = doublePrim;
            return this;
        }

        Boolean getBoolObj() {
            return boolObj;
        }

        GetterSetterAllTypesEntity setBoolObj(Boolean boolObj) {
            this.boolObj = boolObj;
            return this;
        }

        boolean isBoolPrim() {
            return boolPrim;
        }

        GetterSetterAllTypesEntity setBoolPrim(boolean boolPrim) {
            this.boolPrim = boolPrim;
            return this;
        }

        String getString() {
            return string;
        }

        GetterSetterAllTypesEntity setString(String string) {
            this.string = string;
            return this;
        }

        Instant getInstant() {
            return instant;
        }

        GetterSetterAllTypesEntity setInstant(Instant instant) {
            this.instant = instant;
            return this;
        }

        LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        GetterSetterAllTypesEntity setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
            return this;
        }

        LocalDate getLocalDate() {
            return localDate;
        }

        GetterSetterAllTypesEntity setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
            return this;
        }

        OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        GetterSetterAllTypesEntity setOffsetDateTime(OffsetDateTime offsetDateTime) {
            this.offsetDateTime = offsetDateTime;
            return this;
        }

        Date getDate() {
            return date;
        }

        GetterSetterAllTypesEntity setDate(Date date) {
            this.date = date;
            return this;
        }

        Timestamp getTimestamp() {
            return timestamp;
        }

        GetterSetterAllTypesEntity setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        byte[] getByteArray() {
            return byteArray;
        }

        GetterSetterAllTypesEntity setByteArray(byte[] byteArray) {
            this.byteArray = byteArray;
            return this;
        }

        UUID getUuid() {
            return uuid;
        }

        GetterSetterAllTypesEntity setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        ExampleEnum getExampleEnum() {
            return exampleEnum;
        }

        GetterSetterAllTypesEntity setExampleEnum(ExampleEnum exampleEnum) {
            this.exampleEnum = exampleEnum;
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
            clone.exampleEnum = exampleEnum;
            return clone;
        }
    }
}
