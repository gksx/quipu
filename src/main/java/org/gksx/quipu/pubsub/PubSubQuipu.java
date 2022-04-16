package org.gksx.quipu.pubsub;

public abstract class PubSubQuipu {
    
    private String channel;
    
    public abstract PubSubQuipu subscribe(String channel);
    public abstract void listen(OnMessageAction onMessageAction);

    public abstract void close();

    public void setChannel(String channel){
        this.channel = channel;
    }

    public boolean isEventFromCurrentChannel(String recievingChannel){
        return recievingChannel.equals(channel);
    }
}
