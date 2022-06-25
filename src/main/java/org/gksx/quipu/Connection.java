package org.gksx.quipu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class Connection {

    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;

    Connection(Configuration configuration) {
        try {
            socket = new Socket(configuration.getUri(), configuration.getPort());
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();               
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    char read() {
        try {
            return (char) inputStream.read();
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    void close() { 
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    byte[] read(int len) {
        try {
            byte[] buf = new byte[len];
            int read = inputStream.read(buf, 0, len);
            
            if (read != len)
                throw new IOException("something went wrong");

            return buf;
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    void moveToEndOfLine() {
        try {
            int next = 0;
            while (next != -1){
                var q = inputStream.read();
                if (q == RespConstants.CRLF[0]){
                    var s = inputStream.read();
                    if (s == RespConstants.CRLF[1]){
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
                if (next == RespConstants.CRLF[0]){
                    var s = inputStream.read();
                    if (s == RespConstants.CRLF[1]){
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

    void writeToServer(byte[] formatted) {
        try {
            outputStream.write(formatted);
            outputStream.flush();
        } catch (IOException e) {
            throw new QuipuException(e.getMessage(), e);
        }
    }

    boolean isOpen(){
        return !this.socket.isClosed();
    }
}