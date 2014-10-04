package org.leialearns.graph.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectCache<T> {
    private final String cacheId;
    private final Function<Long,T> retrieve;
    private final Map<Long,T> cache = new HashMap<>();

    public ObjectCache(String cacheId, Function<Long,T> retrieve) {
        if (retrieve == null) {
            throw new IllegalArgumentException("Retrieval function should not be null");
        }
        this.cacheId = cacheId == null || cacheId.length() < 1 ? "?" : cacheId;
        this.retrieve = retrieve;
    }

    public T get(Object id) {
        if (!(id instanceof Long)) {
            throw new IllegalArgumentException("The id should have type Long: " + (id == null ? "null" : id.getClass().getSimpleName()));
        }
        T result;
        if (cache.containsKey(id)) {
            result = cache.get(id);
        } else {
            Long key = (Long) id;
            result = retrieve.apply(key);
            if (result == null) {
                throw new IllegalStateException("No object found for id: " + id + ": " + cacheId);
            }
            cache.put(key, result);
        }
        return result;
    }

}
