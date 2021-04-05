package org.gksx;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        var xedis = new Xedis();
        try {
            
            xedis.startConnection("localhost", 6379);
            
            String q = xedis.call("lrange", "mylist", "0", "-1");
            xedis.stopConnection();

            System.out.println(q);
            

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}


