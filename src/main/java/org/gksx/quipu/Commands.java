package org.gksx.quipu;

import java.io.IOException;
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

    /***
     * 
     * @param key
     * @return
     * @throws IOException
     * @throws QuipuException
     */
    String get(String key);
    /**
     * 
     * @param key
     * @param value
     * @throws IOException
     * @throws QuipuException
     */
    void set(String key, String value);
    
    /**
     * 
     * @param key
     * @param value
     */
    void set(String key, Long value);

    /**
     * 
     * @param key
     * @param seconds
     * @param value
     */
    void setEx(String key, Long seconds, String value);
    
    /**
     * awefawe f
     * @param key
     * @return
     * @throws IOException
     * @throws QuipuException
     */
    Long incr(String key);

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    Long incrBy(String key, Long value);

    /**
     * 
     * @param key
     * @return
     */
    Long ttl(String key);

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    Long append(String key, String value);

    /**
     * 
     * @param key
     * @return
     */
    Long del(String key);

    /**
     * 
     * @param key
     * @return
     */
    String getDel(String key);

    /**
     * 
     * @param key
     * @param map
     * @return
     */
    Long hset(String key, Map<String, String> map);
}
