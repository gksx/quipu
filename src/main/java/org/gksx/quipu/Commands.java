package org.gksx.quipu;

import java.io.IOException;

public interface Commands {

    static final String GET = "GET";
    static final String SET = "SET";
    static final String INCR = "INCR";
    static final String INCRBY = "INCRBY";
    static final String SETEX = "SETEX";
    static final String TTL = "TTL";


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
}
