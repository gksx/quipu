package org.gksx.quipu;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

class StreamHandler {

    private Connection connection;

    public StreamHandler(Connection connection){
        this.connection = connection;
    }

    public Object proccessReply() {
        char prefix = connection.read();

        switch (prefix){
            case RespConstants.DOLLAR_BYTE: {
                int len = respLength();
                if (len == RespConstants.NILVALUE)
                    return null;
                var q = parseBulkString(len);
                return q;
            }
            case RespConstants.ASTERISK_BYTE:
                return bulkArray();                
            case RespConstants.PLUS_BYTE:
                return connection.readLine();
            case RespConstants.COLON_BYTE:
                return connection.readLine();
            case RespConstants.MINUS_BYTE:{
                String errorMessage = new String(connection.readLine());
                throw new QuipuException(errorMessage);
            }
            default:
                throw new QuipuException("somethin went wrong");
        }                  
    }

    public Object prepareArgsProcessReply(String... args) {
        var formatted = StreamHandler.toBulkArray(args);
        connection.writeToServer(formatted);
        return proccessReply();
    }

    private byte[] parseBulkString(int len){

        if (len == 0) return null;
        
        byte[] buf = new byte[len];
        
        connection.readBuf(buf, 0, len);
        connection.moveToEndOfLine();
        return buf;
    }

    public String[] bulkArray() {
        int elemnts = respLength();

        String[] list = new String[elemnts];

        for(var i = 0; i < elemnts; i++) {
            list[i] = new String((byte[])proccessReply());
        }

        return list;
    }

    private int respLength(){
        int len = 0;

        char p = connection.read();

        while (p != RespConstants.CARRIAGE_RETURN){
            if (p == '-'){
                connection.moveToEndOfLine();
                return RespConstants.NILVALUE;
            }

            len = (len*10) + (p - '0');
            p = connection.read();
        }
        connection.read();
        
        return len;
    }

    public static byte[] toBulkArray(String... args){
        
        var buffer = new ByteArrayOutputStream();

        var first = String.format("*%d%s", args.length, RespConstants.CARRIAGE_RETURN_LINE_FEED).getBytes(StandardCharsets.UTF_8);
        
        buffer.writeBytes(first);

        for (String arg : args) {
            var argAsBytes = arg.getBytes(StandardCharsets.UTF_8);
            
            var arglen = Integer.toString(argAsBytes.length).getBytes();

            buffer.writeBytes(RespConstants.colon);
            buffer.writeBytes(arglen);
            buffer.writeBytes(RespConstants.CRLF);
            
            buffer.writeBytes(argAsBytes);
            buffer.writeBytes(RespConstants.CRLF);
        }

        return buffer.toByteArray();
    }

    public void close(){
        connection.close();
    }
}
