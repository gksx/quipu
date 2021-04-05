package org.gksx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Xedis {

    public static final char DOLLAR_BYTE = '$';
    public static final char ASTERISK_BYTE = '*';
    public static final char PLUS_BYTE = '+';
    public static final char MINUS_BYTE = '-';
    public static final char COLON_BYTE = ':';
    private Socket clientSocket;
    private BufferedReader in;
    private OutputStream outputStream;
    private static final String CARRIAGE_RETURN = "\r\n";
    private int numberOfElements = 0;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        outputStream = clientSocket.getOutputStream();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String call(String... args) throws IOException {
        var formatted = commandBuilder(args);
        outputStream.write(formatted);
        outputStream.flush();

        
        return proccessReply();
    }

    private String proccessReply() throws IOException {
        var suffix = in.readLine();


        switch (suffix.charAt(0)){
            case DOLLAR_BYTE:{
                return in.readLine();
            }
            case ASTERISK_BYTE:{
                var len = Integer.parseInt(suffix.substring(1).trim());
                proccessReply();
                break;
            }
            case PLUS_BYTE:{
                break;
            }
            case MINUS_BYTE:{
                break;
            }
            default:
                break;

        }          
        return null;
    }

    public void stopConnection() throws IOException {
        in.close();
        outputStream.close();
        clientSocket.close();
    }

    static byte[] commandBuilder(String... args){
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("*%d%s", args.length, CARRIAGE_RETURN));

        for (String arg : args) {
            sb.append(String.format("$%d%s", arg.length(), CARRIAGE_RETURN));
            sb.append(String.format("%s%s", arg, CARRIAGE_RETURN));
        }

        return sb.toString().getBytes();
    }
}
