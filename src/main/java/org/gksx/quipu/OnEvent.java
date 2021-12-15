package org.gksx.quipu;

@FunctionalInterface
public interface OnEvent {
    public void perform(String data);
}
