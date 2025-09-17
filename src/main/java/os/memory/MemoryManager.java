package os.memory;

import java.util.*;

import hardware.CPU;
import hardware.Memory;
import hardware.exception.MemoryAccessException;
import os.config.SystemConfig;

/**
 * 操作系统对物理地址的管理
 */
public class MemoryManager {

    private static final int pageSize = SystemConfig.PAGE_SIZE;

    /**
     * 将 PFN 数据写进物理空间内
     * @param pageFrameNumber 页表项 PFN
     * @param pageData 该页数据
     * @return 物理地址
     */
    public static int write(int pageFrameNumber, byte[] pageData) {
        int physicalAddress = pageFrameNumber * pageSize;
        Memory.write(physicalAddress, pageData);
        return physicalAddress;
    }

    // 虚拟地址 -> 物理地址转换
    public static int getRealAddress(int virtualAddress) {
        if (virtualAddress < 0 || virtualAddress >= CPU.Register.LIMIT.get()) {
            throw new MemoryAccessException("Invalid address Kb" + virtualAddress);
        }
        return virtualAddress + CPU.Register.BASE_ADDRESS.get();
    }


}
