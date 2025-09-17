package os.process;

import hardware.CPU;
import os.memory.MemoryManager;
import os.memory.PageTable;
import os.memory.VirtualMemoryManager;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessControlBlock {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int pid;
    private int virtualAddress;

    // 存寄存器的值
    private int baseAddress;
    private int limit;

    private ProcessState processState;

    private List<PageTable> pageTableEntries;

    private ProcessControlBlock(int pid, byte[] bytes) {
        this.pid = pid;
        this.virtualAddress = allocate(bytes, pid);
        processState = ProcessState.READY;
    }

    public static ProcessControlBlock createProcess(byte[] bytes) {
        int pid = count.getAndIncrement();
        return new ProcessControlBlock(pid, bytes);
    }

    /**
     * 为进程分配虚拟内存
     * @param bytes 程序数据
     * @param pid 进程号
     * @return 虚拟内存地址
     */
    private static int allocate(byte[] bytes, int pid) {
        return VirtualMemoryManager.allocate(pid, bytes);
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
