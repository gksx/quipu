package org.gksx.quipu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PubSubQuipuTest {

    @Test
    public void test_subscrie() throws InterruptedException {
        var quipuSubscribeClient = new Quipu();
        var publisher = new Publisher();
        var subscriber = new Subscriber(quipuSubscribeClient);
        var subscriberThread = new Thread(subscriber);

        subscriberThread.start();
        Thread.sleep(1000);//wait one second to start publish
        publisher.start();
        
        publisher.join();
        quipuSubscribeClient.close();
        subscriberThread.join();

        var messagesReceived = subscriber.getValue();
        assertEquals(messagesReceived, 5);
    }

    class Subscriber implements Runnable {
        private volatile int value;
        private Quipu quipu;

        public Subscriber(Quipu quipuSubscribeClient) {
            this.quipu = quipuSubscribeClient;
        }

        @Override
        public void run() {
            try {
                quipu.subscribe("channel")
                    .listen(s -> {
                        value++;
                    });
            } catch(QuipuException qu){} //we dont care
        }

        public int getValue() {
            return value;
        }
    }

    class Publisher extends Thread {
        public void run(){
            var quipu = new Quipu();
    
            for (int i = 0; i < 5; i++){
                quipu.publish("channel", "message : " + i);
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                
            }
            quipu.close();
        }
    }
}
