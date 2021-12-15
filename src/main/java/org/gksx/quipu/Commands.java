package org.gksx.quipu;

import java.util.Map;

public interface Commands {

    static final String GET = "GET";
    static final String SET = "SET";
    static final String INCR = "INCR";
    static final String INCRBY = "INCRBY";
    static final String SETEX = "SETEX";
    static final String TTL = "TTL";
    static final String APPEND ="APPEND";
    static final String DEL = "DEL";
    static final String GETDEL = "GETDEL";
    static final String HSET = "HSET";
    static final String SUBSCRIBE = "SUBSCRIBE";
    static final String UNSUBSCRIBE = "UNSUBSCRIBE";

    String get(String key);
    void set(String key, String value);
    void set(String key, Long value);
    void setEx(String key, Long seconds, String value);
    Long incr(String key);
    Long incrBy(String key, Long value);
    Long ttl(String key);
    Long append(String key, String value);
    Long del(String key);
    String getDel(String key);
    Long hset(String key, Map<String, String> map);
}
