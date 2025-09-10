package org.example.mmu;

public class LimitRegister implements Register{
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