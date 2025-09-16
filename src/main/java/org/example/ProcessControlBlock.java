package org.example;

import org.example.hardware.CPU;
import org.example.mmu.BaseAddressRegister;
import org.example.mmu.LimitRegister;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessControlBlock {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int pid;
    private int baseAddress;
    private int limit;
    private List<CPU.Register> registers;

    private ProcessState processState;

    private List<PageTable> pageTableEntries;

    private ProcessControlBlock(int pid) {
        this.pid = pid;
        registers = new ArrayList<CPU.Register>();
        registers.add(new BaseAddressRegister(1024));
        registers.add(new LimitRegister(1024));
        processState = ProcessState.READY;
    }

    public static ProcessControlBlock createProcess(byte[] bytes) {
        int memoryP = allocate(bytes);
        return new ProcessControlBlock(count.getAndIncrement());
    }

    private static int allocate(byte[] bytes) {
        return 1;
    }

    /**
     * 上下文切换
     */
    public void waitInContext(){
        for (CPU.Register register : registers) {
            if (register instanceof BaseAddressRegister) {
                this.baseAddress = ((BaseAddressRegister) register).getBaseAddressKb();
            } else if (register instanceof LimitRegister) {
                this.limit = ((LimitRegister) register).getLimitKb();
            }
        }
    }

    public void refresh(){
        for (CPU.Register register : registers) {
            if (register instanceof BaseAddressRegister) {
                ((BaseAddressRegister) register).setBaseAddressKb(this.baseAddress);
            } else if (register instanceof LimitRegister) {
                ((LimitRegister) register).setLimitKb(this.limit);
            }
        }
    }

    public int getPid() {
        return pid;
    }

    public List<PageTable> getPageTableEntries() {
        return pageTableEntries;
    }




}

enum ProcessState {
    READY, RUNNING, STOPPED
}
