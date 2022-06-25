package org.gksx.quipu.pool;

import java.util.concurrent.atomic.AtomicBoolean;

import org.gksx.quipu.Quipu;

class PoolInstance {
    private Quipu quipu;
    private AtomicBoolean isInUse;

    public PoolInstance(Quipu quipu){
        this.quipu = quipu;
        this.isInUse = new AtomicBoolean(false);
    }

    public Quipu getQuipu() {
        return quipu;
    }

    public boolean getIsInUse() {
        return isInUse.get();
    }

    public void setIsInUse() {
        this.isInUse.set(true);;
    }

    public void setIsNotInUse(){
        this.isInUse.set(false);
    }
}
