package os.memory;

import java.util.*;

import hardware.CPU;
import hardware.Memory;
import hardware.exception.MemoryAccessException;

/**
 * 操作系统对物理地址的管理
 */
public class MemoryManager {
    private static final int pageSize = 1024;
    private static final BitSet allocatedFrames = new BitSet(Memory.getPhysicalMemorySize() / pageSize);

    private static int allocate(byte[] bytes) {
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


    // 虚拟地址 -> 物理地址转换
    public static int getRealAddress(int virtualAddress) {
        if (virtualAddress < 0 || virtualAddress >= CPU.Register.LIMIT.get()) {
            throw new MemoryAccessException("Invalid address Kb" + virtualAddress);
        }
        return virtualAddress + CPU.Register.BASE_ADDRESS.get();
    }


    private static List<Integer> allocateFrames(int pageNeeded) {
        if (pageNeeded <= 0) {
            return null;
        }

        // 查找连续的可用页帧
        int start = 0;
        while (true) {
            // 找到下一个空闲页帧
            int nextFree = allocatedFrames.nextClearBit(start);
            if (nextFree >= allocatedFrames.size()) {
                return null; // 没有足够的空闲页帧
            }

            // 检查是否有足够的连续空闲页帧
            int end = nextFree;
            while (end < allocatedFrames.size() && !allocatedFrames.get(end) && (end - nextFree + 1) < pageNeeded) {
                end++;
            }

            // 如果找到足够的连续空闲页帧
            if (end - nextFree + 1 >= pageNeeded) {
                List<Integer> allocated = new ArrayList<>();
                // 标记这些页帧为已分配
                for (int i = nextFree; i < nextFree + pageNeeded; i++) {
                    allocatedFrames.set(i);
                    allocated.add(i);
                }
                return allocated;
            }

            // 继续查找
            start = end + 1;
        }
    }

    // 补充释放页帧的方法
    public static void freeFrames(int startFrame, int pageCount) {
        if (startFrame < 0 |pageCount <= 0
                | (startFrame + pageCount) > allocatedFrames.size()) {
            throw new IllegalArgumentException("Invalid frame range");
        }

        for (int i = startFrame; i < startFrame + pageCount; i++) {
            allocatedFrames.clear(i);
        }
    }
}
