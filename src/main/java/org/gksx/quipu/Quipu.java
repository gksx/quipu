package org.gksx.quipu;

import java.io.IOException;
import java.net.Socket;

public class Quipu {

    private static final byte DOLLAR_BYTE = '$';
    private static final byte ASTERISK_BYTE = '*';
    private static final byte PLUS_BYTE = '+';
    private static final byte MINUS_BYTE = '-';
    private static final byte COLON_BYTE = ':';
    
    private Socket clientSocket;
    private QuipuStream quipuStream;
    private static final char CARRIAGE_RETURN = '\r';
    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";
    private String uri = "127.0.0.1";
    private int port = 6379;

    public Quipu(String uri, int port) throws IOException {
        this.uri = uri;
        this.port = port;
        open();
    }

    public Quipu() throws IOException {
        open();
    }

    public void open() throws IOException {
        this.clientSocket = Connection.connectionBuilder(this.uri,this.port)
                                  .getSocket();

        quipuStream = new QuipuStream(clientSocket);                        
                                
    }

    public String call(String... args) throws IOException, QuipuException {
        var formatted = commandBuilder(args);
        quipuStream.getOutputStream().write(formatted);
        quipuStream.getOutputStream().flush();

        return proccessReply();
    }

    public int parse() throws IOException, QuipuException{
        int len = 0;

        char p = quipuStream.read();

        while (p != CARRIAGE_RETURN){
            
            if (p == '-'){
                moveToEndOfLine();
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
        
        char[] buf = new char[len];

        quipuStream.getBufferedReader().read(buf, 0, len);
        moveToEndOfLine();
        return String.valueOf(buf);
    }

    public void moveToEndOfLine() throws QuipuException, IOException {
        quipuStream.getBufferedReader().readLine();
    }

    private String proccessReply() throws IOException, QuipuException {
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
                return quipuStream.getBufferedReader().readLine();
            }
            case MINUS_BYTE:{
                String errorMessage = quipuStream.getBufferedReader().readLine();
                throw new QuipuException(errorMessage);
            }
            case COLON_BYTE:{
                return quipuStream.getBufferedReader().readLine();
            }
            default:
                throw new QuipuException("somethin went wrong");

        }          
        
    }

    private String parseBulkArray() throws IOException, QuipuException {

        int elemnts = parse();

        String reply = "";

        for(var i = 0; i < elemnts; i++) {
            reply = reply + ";" + proccessReply();
        }

        return reply;
    }

    public void close() throws IOException {
        clientSocket.close();
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
