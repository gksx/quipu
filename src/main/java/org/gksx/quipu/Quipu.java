package org.gksx.quipu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class Quipu {

    private static final byte DOLLAR_BYTE = '$';
    private static final byte ASTERISK_BYTE = '*';
    private static final byte PLUS_BYTE = '+';
    private static final byte MINUS_BYTE = '-';
    private static final byte COLON_BYTE = ':';
    
    private Socket clientSocket;
    private BufferedReader in;
    private OutputStream outputStream;
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
        clientSocket = new Socket(uri, port);
        outputStream = clientSocket.getOutputStream();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
    }

    public String call(String... args) throws IOException, QuipuException {
        var formatted = commandBuilder(args);
        outputStream.write(formatted);
        outputStream.flush();

        return proccessReply();
    }

    public int parse() throws IOException, QuipuException{
        int len = 0;

        char p = (char)in.read();

        while (p != CARRIAGE_RETURN){
            
            if (p == '-'){
                moveToEndOfLine();
                return 0;
            }

            len = (len*10) + (p - '0');
            p = (char)in.read();
        }
        in.read();
        
        return len;
    }

    private String parseBulkString(int len) throws IOException, QuipuException{

        if (len == 0) return null;
        
        char[] buf = new char[len];

        in.read(buf, 0, len);
        moveToEndOfLine();
        return String.valueOf(buf);
    }

    public void moveToEndOfLine() throws QuipuException, IOException {
        in.readLine();
    }

    private String proccessReply() throws IOException, QuipuException {
        char prefix = (char)in.read();

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
                return in.readLine();
            }
            case MINUS_BYTE:{
                String errorMessage = in.readLine();
                throw new QuipuException(errorMessage);
            }
            case COLON_BYTE:{
                return in.readLine();
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
        in.close();
        outputStream.close();
        clientSocket.close();
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
