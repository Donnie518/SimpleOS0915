package os.memory;

import hardware.Memory;
import os.config.SystemConfig;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class VirtualMemoryManager {

    private static final int pageSize = SystemConfig.PAGE_SIZE;

    private static final BitSet allocatedPhysicalFrames = new BitSet(Memory.getPhysicalMemorySize() / pageSize);

    /**
     * 给一个进程开内存，返回虚拟地址
     * @param pid 进程 pid
     * @param bytes 程序数据
     * @return 虚拟地址
     */
    public static int allocate(int pid, byte[] bytes) {
        int pageNeeded = (bytes.length + pageSize - 1) / pageSize;

        List<Integer> pageFrames = allocateFrames(pageNeeded);

        if (pageFrames == null) {
            return 0;
        }

        // 写入物理内存
        for (int i = 0; i < pageNeeded; i++) {
            int offset = i * pageSize;
            int length = Math.min(bytes.length - offset, pageSize);
            byte[] pageData = Arrays.copyOfRange(bytes, offset, offset + length);
            Memory.write(pageFrames.get(i), pageData);
        }

        return pageFrames.getFirst() * pageSize;
    }
}
