package org.gksx;

import java.io.IOException;
import java.net.UnknownHostException;

import org.gksx.Quipu.Quipu;
import org.gksx.Quipu.QuipuException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            
            var quipu = new Quipu("localhost", 6379)
                .startConnection();
            

            String h = quipu.call("ret", "tja", "varför");

            System.out.println(h);

            String q = quipu.call("get", "hej");
            

            System.out.println(q);
            

        } catch (UnknownHostException e) {
            
            e.printStackTrace();
        } catch (IOException e) {
            
            e.printStackTrace();
        } catch (QuipuException e){
            e.printStackTrace();

        }
    }
}


