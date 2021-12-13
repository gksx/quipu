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
    String get(String key) throws IOException, QuipuException;
    /**
     * 
     * @param key
     * @param value
     * @throws IOException
     * @throws QuipuException
     */
    void set(String key, String value) throws IOException, QuipuException;

    void set(String key, Long value) throws IOException, QuipuException;

    void setEx(String key, Long seconds, String value) throws IOException, QuipuException;
    
    /**
     * awefawe f
     * @param key
     * @return
     * @throws IOException
     * @throws QuipuException
     */
    Long incr(String key) throws IOException, QuipuException;

    Long incrBy(String key, Long value) throws IOException, QuipuException;

    Long ttl(String key) throws IOException, QuipuException;

    Long append(String key, String value) throws IOException, QuipuException;

    Long del(String key) throws IOException, QuipuException;

    String getDel(String key) throws IOException, QuipuException;

    Long hset(String key, Map<String, String> map) throws IOException, QuipuException;
}
