package org.example.mmu;

import org.example.hardware.CPU;

public class BaseAddressRegister extends CPU.Register {
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
