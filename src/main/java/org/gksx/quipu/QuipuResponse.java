package org.gksx.quipu;

import java.util.ArrayList;
import java.util.List;

/**
 * Only for internal use, never expose to user
 */
class QuipuResponse {
    private byte[] content;
    private ResponseType responseType;
    private List<QuipuResponse> lsit;

    QuipuResponse(byte[] buffer, ResponseType responseType) {
        this.content = buffer;
        this.responseType = responseType;
        this.lsit = new ArrayList<>();
    }

    void add(QuipuResponse quipuResponse){
        lsit.add(quipuResponse);
    }
    
    byte[] content(){
        return content;
    }
}

/**
 * So our converter can know which type to convert to
 */
enum ResponseType {
    STRING,LONG,LISTELEMENT
}