package org.example.mmu;

public class BaseAddressRegister implements Register {
    private int baseAddressKb;



    public BaseAddressRegister(int baseAddressKb) {
        this.baseAddressKb = baseAddressKb;
    }

    public int getBaseAddressKb() {
        return baseAddressKb;
    }

    public void setBaseAddressKb(int baseAddressKb) {
        this.baseAddressKb = baseAddressKb;
    }
}
