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

    public AtomicBoolean getIsInUse() {
        return isInUse;
    }

    public void setIsInUse() {
        this.isInUse.set(true);;
    }

    public void setIsNotInUse(){
        this.isInUse.set(false);
    }
    public void setQuipu(Quipu quipu) {
        this.quipu = quipu;
    }
}
