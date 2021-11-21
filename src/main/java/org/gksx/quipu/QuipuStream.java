package org.gksx.quipu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class QuipuStream {

    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;
    private static final byte[] CARRIAGE_RETURN_LINE_FEED = {'\r', '\n'};

    public QuipuStream(String uri, int port) throws IOException {
        socket = new Socket(uri, port);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();   
    }

    public char read() throws IOException{
        return(char) inputStream.read();
    }

    public void close() throws IOException{
        outputStream.close();
        inputStream.close();
        socket.close();
    }

    public int readBuf(byte[] buf, int i, int len) throws IOException {
    
        return inputStream.read(buf, i, len);
    }

    public void moveToEndOfLine() throws IOException {
        while (true){
            var q = inputStream.read();
            if (q == CARRIAGE_RETURN_LINE_FEED[0]){
                var s = inputStream.read();
                if (s == CARRIAGE_RETURN_LINE_FEED[1]){
                    break;
                }
            }
        }
    }

    public byte[] readLine() throws IOException{
        var buf = new ByteArrayOutputStream();
        int next = 0;
        
        while(next != -1) {
            next = inputStream.read();
            if (next == CARRIAGE_RETURN_LINE_FEED[0]){
                var s = inputStream.read();
                if (s == CARRIAGE_RETURN_LINE_FEED[1]){
                    break;
                }
            }
            buf.write(next);
        }

        return buf.toByteArray();
    }

    public void writeAndFlush(byte[] formatted) throws IOException {
        outputStream.write(formatted);
        outputStream.flush();
    }

    public boolean isOpen(){
        return this.socket.isConnected();
    }
}