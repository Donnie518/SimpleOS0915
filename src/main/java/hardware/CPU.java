package hardware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CPU - 模拟
  */
public class CPU {
    public enum Register {
        BASE_ADDRESS,
        LIMIT;

        private final int[] values = new int[Register.values().length];

        public int get() {
            return values[ordinal()];
        }

        public void set(int value) {
            values[ordinal()] = value;
        }
    }

    public static class CU {

    }

    public static class ALU {

    }

    public static class Cache {
        public static byte[] allocate(int size) {
            return new byte[size];
        }
    }

    public static class TLB {

        // TLB数据存储
        private static final Map<Integer, Integer> tlbCache = new ConcurrentHashMap<>();

        // TLB查找方法
        public static Integer lookup(int virtualPageNumber) {
            return tlbCache.get(virtualPageNumber);
        }

        // TLB更新方法
        public static void update(int virtualPageNumber, int pageFrameNumber) {
            tlbCache.put(virtualPageNumber, pageFrameNumber);
        }

        // 清空TLB
        public static void flush() {
            tlbCache.clear();
        }
    }
}
