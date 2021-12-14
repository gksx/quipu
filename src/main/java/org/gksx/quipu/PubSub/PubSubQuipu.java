package org.gksx.quipu.PubSub;

public abstract class PubSubQuipu {
    public abstract PubSubQuipu subscribe(String channel);
    public abstract void onEvent(OnEvent onEvent);
}
