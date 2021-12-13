package org.gksx.quipu.PubSub;

import java.io.IOException;

import org.gksx.quipu.QuipuException;

public abstract class PubSubQuipu {
    public abstract PubSubQuipu subscribe(String channel) throws IOException, QuipuException;
    public abstract void onEvent(OnEvent onEvent);
}
