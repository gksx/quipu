package org.gksx.quipu;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

class StreamHandler {

    private Connection connection;

    public StreamHandler(Connection connection){
        this.connection = connection;
    }

    public QuipuResponse processReply(QuipuResponse qr) {
        
        char prefix = connection.read();

        switch (prefix){
            case RespConstants.DOLLAR_BYTE: {
                int len = respLength();
                if (len == RespConstants.NILVALUE)
                    return null;
                byte[] content = parseBulkString(len);
                return new QuipuResponse(content, ResponseType.STRING);
            }
            case RespConstants.ASTERISK_BYTE:
                bulkArrayew();
                break;
                // list.add(bulkArrayew());
            case RespConstants.PLUS_BYTE:
                return new QuipuResponse(connection.readLine(), ResponseType.STRING);
            case RespConstants.COLON_BYTE:
                return new QuipuResponse(connection.readLine(), ResponseType.LONG);
            case RespConstants.MINUS_BYTE:{
                String errorMessage = new String(connection.readLine());
                throw new QuipuException(errorMessage);
            }
        }

        throw new QuipuException("somethin went wrong");
    }

    public QuipuResponse processReply() {
        return processReply(null);
    }

    public QuipuResponse prepareArgsProcessReply(String... args) {
        var formatted = toBulkArray(args);
        connection.writeToServer(formatted);
        return processReply();
    }

    private byte[] parseBulkString(int len){

        if (len == 0) return null;
                
        byte[] content = connection.read(len);
        connection.moveToEndOfLine();
        return content;
    }

    public QuipuResponse bulkArrayew() {
        int elemnts = respLength();
        QuipuResponse head = null;
        for(var i = 0; i < elemnts; i++) {
            
            var q = processReply();
        }

        return head;
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
