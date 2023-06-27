package com.pwinckles.jdbcgen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePatch {

    private final Map<String, Object> data = new HashMap<>();

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    protected void put(String key, Object value) {
        data.put(key, value);
    }
}
