package org.gksx.quipu;

public abstract class PubSubQuipu {
    
    private String channel;
    
    public abstract PubSubQuipu subscribe(String channel);
    public abstract void listen(OnEvent onEvent);

    public abstract void close();

    void setChannel(String channel){
        this.channel = channel;
    }

    public boolean isEventFromCurrentChannel(String recievingChannel){
        return recievingChannel.equals(channel);
    }
}
