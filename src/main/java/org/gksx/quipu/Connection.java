package org.gksx.quipu;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    private Socket socket;
    private Connection(String uri, int port){
        try {
            socket = new Socket(uri, port);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Socket getSocket(){
        return socket;
    }

    public static Connection connectionBuilder(String uri, int port){
        return new Connection(uri, port);
    }
}