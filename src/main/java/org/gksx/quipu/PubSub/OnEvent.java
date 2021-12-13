package org.gksx.quipu.PubSub;

@FunctionalInterface
public interface OnEvent {
    public void perform(String data);
}
