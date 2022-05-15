package org.gksx.quipu;

import java.util.ArrayList;
import java.util.List;

/**
 * Only for internal use, never expose to user
 */
class QuipuResponse {
    private byte[] buffer;
    private ResponseType responseType;
    private List<QuipuResponse> lsit;

    QuipuResponse(byte[] buffer, ResponseType responseType) {
        this.buffer = buffer;
        this.responseType = responseType;
        this.lsit = new ArrayList<>();
    }

    void add(QuipuResponse quipuResponse){
        lsit.add(quipuResponse);
    }
    
    byte[] buffer(){
        return buffer;
    }
}

/**
 * So our converter can know which type to convert to
 */
enum ResponseType {
    STRING,LONG,LISTELEMENT
}