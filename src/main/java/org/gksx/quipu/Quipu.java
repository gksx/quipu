package org.gksx.quipu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Quipu {

    private static final byte DOLLAR_BYTE = '$';
    private static final byte ASTERISK_BYTE = '*';
    private static final byte PLUS_BYTE = '+';
    private static final byte MINUS_BYTE = '-';
    private static final byte COLON_BYTE = ':';   
    private static final char CARRIAGE_RETURN = '\r';
    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";
    private String uri = "127.0.0.1";
    private int port = 6379;

    private QuipuStream quipuStream;

    public Quipu(String uri, int port) throws IOException {
        this.uri = uri;
        this.port = port;
        openQuipuStream();
    }

    public Quipu() throws IOException {
        openQuipuStream();
    }

    public Quipu(QuipuStream quipuStream){
        this.quipuStream = quipuStream;
    }

    private void openQuipuStream() throws IOException {
        quipuStream = new QuipuStream(this.uri, this.port);                                                        
    }

    public Object call(String... args) throws IOException, QuipuException {
        var formatted = commandBuilder(args);
        quipuStream.writeAndFlush(formatted);
        return proccessReply();
    }

    public int parse() throws IOException, QuipuException{
        int len = 0;

        char p = quipuStream.read();

        while (p != CARRIAGE_RETURN){
            
            if (p == '-'){
                quipuStream.moveToEndOfLine();
                return 0;
            }

            len = (len*10) + (p - '0');
            p = quipuStream.read();
        }
        quipuStream.read();
        
        return len;
    }

    private String parseBulkString(int len) throws IOException, QuipuException{

        if (len == 0) return null;
        
        byte[] buf = new byte[len];

        
        quipuStream.readBuf(buf, 0, len);
        quipuStream.moveToEndOfLine();
        return String.valueOf(buf);
    }

    private Object proccessReply() throws IOException, QuipuException {
        char prefix = quipuStream.read();

        switch (prefix){
            case DOLLAR_BYTE:{
                int len = parse();
                var q = parseBulkString(len);
                return q == null ? null : q.toString();
            }
            case ASTERISK_BYTE:{
                return parseBulkArray();                
            }
            case PLUS_BYTE:{
                return quipuStream.readLine();
            }
            case MINUS_BYTE:{
                String errorMessage = new String(quipuStream.readLine());
                throw new QuipuException(errorMessage);
            }
            case COLON_BYTE:{
                return quipuStream.readLine();
            }
            default:
                throw new QuipuException("somethin went wrong");

        }          
        
    }

    private List<String> parseBulkArray() throws IOException, QuipuException {
        int elemnts = parse();

        List<String> list = new ArrayList<>();

        
        for(var i = 0; i < elemnts; i++) {
            list.add((String)proccessReply());
        }

        return list;
    }

    public void close() throws IOException {
        quipuStream.close();
    }

    public static byte[] commandBuilder(String... args){
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("*%d%s", args.length, CARRIAGE_RETURN_LINE_FEED));

        for (String arg : args) {
            sb.append(String.format("$%d%s", arg.length(), CARRIAGE_RETURN_LINE_FEED));
            sb.append(String.format("%s%s", arg, CARRIAGE_RETURN_LINE_FEED));
        }

        return sb.toString().getBytes();
    }
}
