package org.gksx.quipu;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class CommandFactory {
    
    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";
    private static final byte[] CRLF = {'\r','\n'};
    private static byte[] colon = {'$'};

    public static byte[] buildSafeEncoding(String... args){
        
        var buffer = new ByteArrayOutputStream();

        var first = String.format("*%d%s", args.length, CARRIAGE_RETURN_LINE_FEED).getBytes(StandardCharsets.UTF_8);
        
        buffer.writeBytes(first);

        for (String arg : args) {
            var argAsBytes = arg.getBytes(StandardCharsets.UTF_8);
            
            var arglen = Integer.toString(argAsBytes.length).getBytes();

            buffer.writeBytes(colon);
            buffer.writeBytes(arglen);
            buffer.writeBytes(CRLF);
            
            buffer.writeBytes(argAsBytes);
            buffer.writeBytes(CRLF);
        }

        return buffer.toByteArray();
    }
}
