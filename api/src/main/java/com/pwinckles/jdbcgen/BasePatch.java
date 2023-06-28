package com.pwinckles.jdbcgen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Models a partial entity update. Use implementations of this class to update a subset of an entity's fields.
 * Implementations are generated at compile time.
 */
public abstract class BasePatch {

    private final Map<String, Object> data = new HashMap<>();

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    protected void put(String key, Object value) {
        data.put(key, value);
    }
}
