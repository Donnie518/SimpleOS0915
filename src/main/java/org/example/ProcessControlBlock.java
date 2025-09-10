package org.example;

import org.example.mmu.BaseAddressRegister;
import org.example.mmu.LimitRegister;
import org.example.mmu.Register;

import java.util.ArrayList;
import java.util.List;

public class ProcessControlBlock {
    private int pid;
    private List<Register> registers;

    private ProcessState processState;

    public ProcessControlBlock(int pid) {
        this.pid = pid;
        registers = new ArrayList<Register>();
        registers.add(new BaseAddressRegister(1024));
        registers.add(new LimitRegister(1024));
        processState = ProcessState.READY;
    }
}

enum ProcessState {
    READY, RUNNING, STOPPED
}