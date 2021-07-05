package org.gksx.quipu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class QuipuStream {

    private OutputStream outputStream;
    private BufferedReader bufferedReader;

    public QuipuStream(Socket socket) throws IOException{
        outputStream = socket.getOutputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   
    }
    
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }

    public char read() throws IOException{
        return(char) bufferedReader.read();
    }

    public void close() throws IOException{
        outputStream.close();
        bufferedReader.close();
    }
}
