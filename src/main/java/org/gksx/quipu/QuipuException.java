package org.gksx.quipu;

import java.io.IOException;

public class QuipuException extends RuntimeException {
    public QuipuException(String message){
        super(message);
    }

    public QuipuException(String message, IOException cause){
        super(message, cause);
    }
}
