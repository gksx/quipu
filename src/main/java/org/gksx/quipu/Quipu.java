package org.gksx.quipu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Quipu {

    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';
    public static final byte PLUS_BYTE = '+';
    public static final byte MINUS_BYTE = '-';
    public static final byte COLON_BYTE = ':';
    
    private Socket clientSocket;
    private BufferedReader in;
    private OutputStream outputStream;
    private static final char CARRIAGE_RETURN = '\r';
    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";
    private String uri;
    private int port;

    public Quipu(String uri, int port) {
        this.uri = uri;
        this.port = port;
    }

    public Quipu startConnection() throws IOException {
        clientSocket = new Socket(uri, port);
        outputStream = clientSocket.getOutputStream();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return this;
    }

    public String call(String... args) throws IOException, QuipuException {
        var formatted = commandBuilder(args);
        outputStream.write(formatted);
        outputStream.flush();

        return proccessReply();
    }



    public int parse() throws IOException{
        int len = 0;

        char p = (char)in.read();

        while (p != CARRIAGE_RETURN){
            len = (len*10) + (p - '0');
            p = (char)in.read();
        }
        in.read();
        
        return len;
    }

    public String parseBulkString(int len) throws IOException, QuipuException{
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
                return q.toString();
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
            default:
                throw new QuipuException("somethin went wrong");

        }          
        
    }

    private String parseBulkArray() {
        return null;
    }

    public void stopConnection() throws IOException {
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
