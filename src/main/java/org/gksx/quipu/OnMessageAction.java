package org.gksx.quipu;

@FunctionalInterface
public interface OnMessageAction {
    public void perform(String data);
}
