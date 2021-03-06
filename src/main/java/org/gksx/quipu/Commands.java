package org.gksx.quipu;

import java.util.List;
import java.util.Map;

public interface Commands {
    
    class Keys {
        private Keys(){ }
        
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
        static final String STRLEN = "STRLEN";
        static final String MULTI = "MULTI";
        static final String EXEC = "EXEC";
        static final String PUBLISH = "PUBLISH";
        static final String LPUSH = "LPUSH";
        static final String LPOP = "LPOP";
        static final String LLEN = "LLEN";
        static final String SETRANGE = "SETRANGE";
        static final String HGET = "HGET";
        static final String HGETALL = "HGETALL";
        static final String XADD = "XADD";
        static final String XRANGE = "XRANGE";
    }
      
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
    Long strlen(String key);
    String multi();
    String[] exec();
    Long publish(String channel, String message);
    Long lpush(String key, String element);
    Long lpush(String key, List<String> elements);
    String lpop(String key);
    String[] lpop(String key, Long count);
    Long llen(String key);
    Long setRange(String key, Long offset, String value);
    Map<String, String> hgetAll(String key);
    String hget(String key, String field);

    interface Stream {
        String xAdd(String stream, String entryKey, Map<String, String> values);
    }
}
