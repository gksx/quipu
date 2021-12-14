package org.gksx.quipu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {

    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;
    private static final byte[] CARRIAGE_RETURN_LINE_FEED = {'\r', '\n'};

    public Connection(String uri, int port) {
        try {
            socket = new Socket(uri, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();               
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e.getCause());
        }
    }

    public char read() {
        try{
            return (char) inputStream.read();
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    public void close() { 
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    protected int readBuf(byte[] buf, int i, int len) {
        try {
            return inputStream.read(buf, i, len);
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    void moveToEndOfLine() {
        try {
            while (true){
                var q = inputStream.read();
                if (q == CARRIAGE_RETURN_LINE_FEED[0]){
                    var s = inputStream.read();
                    if (s == CARRIAGE_RETURN_LINE_FEED[1]){
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }       
    }

    byte[] readLine(){
        var buf = new ByteArrayOutputStream();
        int next = 0;
        
        while(next != -1) {
            try {
                next = inputStream.read();
                if (next == CARRIAGE_RETURN_LINE_FEED[0]){
                    var s = inputStream.read();
                    if (s == CARRIAGE_RETURN_LINE_FEED[1]){
                        break;
                    }
                }
                buf.write(next);
                
            } catch (IOException e) {
                new QuipuException(e.getMessage(), e);
            }
        }

        return buf.toByteArray();
    }

    public void writeAndFlush(byte[] formatted) {
        try {
            outputStream.write(formatted);
            outputStream.flush();
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    public boolean isOpen(){
        return !this.socket.isClosed();
    }
}