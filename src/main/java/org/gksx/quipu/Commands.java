package org.gksx.quipu;

import java.io.IOException;

public interface Commands {

    public static final String GET = "GET";
    public static final String SET = "SET";
    public static final String INCR = "INCR";

    String get(String key) throws IOException, QuipuException;
    void set(String key, String value) throws IOException, QuipuException;
    Long incr(String key) throws IOException, QuipuException;
}
