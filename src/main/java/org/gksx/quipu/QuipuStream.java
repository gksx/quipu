package org.gksx.quipu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class QuipuStream {

    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private Socket socket;

    public QuipuStream(String uri, int port) throws IOException {
        socket = new Socket(uri, port);
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
        socket.close();
    }

    public int readBuf(char[] buf, int i, int len) throws IOException {
        return bufferedReader.read(buf, i, len);
    }

    public void moveToEndOfLine() throws IOException {
        bufferedReader.readLine();
    }

    public void writeAndFlush(byte[] formatted) throws IOException {
        outputStream.write(formatted);
        outputStream.flush();
    }


}
