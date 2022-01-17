package mx.kenzie.mirror;

import mx.kenzie.mimic.MethodErasure;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodCache {
    
    protected final Map<MethodErasure, MethodAccessor<?>> map;
    
    protected MethodCache(Map<MethodErasure, MethodAccessor<?>> map) {
        this.map = map;
    }
    
    public static MethodCache direct() {
        return new MethodCache(new HashMap<>());
    }
    
    public static MethodCache concurrent() {
        return new MethodCache(new ConcurrentHashMap<>());
    }
    
    public static MethodCache linked() {
        return new MethodCache(new LinkedHashMap<>());
    }
    
    public void cache(MethodErasure erasure, MethodAccessor<?> accessor) {
        this.map.put(erasure, accessor);
    }
    
    public MethodAccessor<?> get(MethodErasure erasure) {
        return this.map.get(erasure);
    }
    
    public boolean has(MethodErasure erasure) {
        return this.map.containsKey(erasure);
    }
    
}
