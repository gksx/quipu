package org.gksx.quipu.pubsub;

@FunctionalInterface
public interface OnMessageAction {
    public void perform(String data);
}
