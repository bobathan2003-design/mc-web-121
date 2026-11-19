// src/main/java/com/mcweb/registry/ObjectRegistry.java
package com.mcweb.registry;

import org.teavm.jso.JSObject;
import java.util.HashMap;
import java.util.Map;

public class ObjectRegistry {
    private final Map<Integer, JSObject> objects = new HashMap<>();
    private int nextId = 1;

    public int register(JSObject obj) {
        int id = nextId++;
        objects.put(id, obj);
        return id;
    }

    public JSObject get(int id) {
        if (id == 0) return null;
        return objects.get(id);
    }

    public void delete(int id) {
        objects.remove(id);
    }
}
