package os.process;

import hardware.CPU;
import os.memory.PageTable;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessControlBlock {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int pid;
    private int baseAddress;
    private int limit;

    private ProcessState processState;

    private List<PageTable> pageTableEntries;

    private ProcessControlBlock(int pid) {
        this.pid = pid;
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
     * 上下文切换-挂起
     */
    public void waitInContext(){
        // 寄存器
        this.baseAddress = CPU.Register.BASE_ADDRESS.get();
        this.limit = CPU.Register.LIMIT.get();
        // TLB flush
        CPU.TLB.flush();

    }

    /**
     * 上下文切换-恢复
     */
    public void refresh(){
        // 寄存器

        CPU.Register.LIMIT.set(limit);
        CPU.Register.BASE_ADDRESS.set(baseAddress);


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
