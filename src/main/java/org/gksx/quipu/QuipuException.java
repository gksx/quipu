package org.gksx.quipu;

public class QuipuException extends RuntimeException {
    public QuipuException(String message){
        super(message);
    }

    public QuipuException(String message, Throwable cause){
        super(message, cause);
    }
}
