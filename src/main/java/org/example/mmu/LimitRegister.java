package org.example.mmu;

import org.example.hardware.CPU;

public class LimitRegister extends CPU.Register {
    private int limitKb;

    public LimitRegister(int limitKb) {
        this.limitKb = limitKb;
    }

    public int getLimitKb() {
        return limitKb;
    }

    public void setLimitKb(int limitKb) {
        this.limitKb = limitKb;
    }
}