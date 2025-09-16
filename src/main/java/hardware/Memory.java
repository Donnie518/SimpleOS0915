package hardware;

import hardware.exception.MemoryAccessException;

/**
 * 物理内存 - 模拟
 */
public class Memory {
    // 1MB 物理内存
    private static final int PHYSICAL_MEMORY_SIZE = 1024 * 1024;

    // 内存数组
    private static final byte[] MEMORY_DATA = new byte[PHYSICAL_MEMORY_SIZE];

    // 物理内存-读取
    public static byte[] read(int address, int length) {
        // 这里应该是读进 CPU 里，先简化到 new 一个内存
        byte[] buffer = CPU.Cache.allocate(length);
        System.arraycopy(MEMORY_DATA, address, buffer, 0, length);
        return buffer;
    }

    // 物理内存-写入
    public static void write(int address, byte[] data) {
        System.arraycopy(data, 0, MEMORY_DATA, address, data.length);
    }

    public static int getPhysicalMemorySize(){
        return PHYSICAL_MEMORY_SIZE;
    }

}
