package org.example.mmu;

import org.example.hardware.CPU;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum TranslationLookasideBuffer {

        INSTANCE;
    // TLB数据存储
    private Map<Integer, Integer> tlbCache = new ConcurrentHashMap<>();

    // TLB查找方法
    public Integer lookup(int virtualPageNumber) {
        return tlbCache.get(virtualPageNumber);
    }

    // TLB更新方法
    public void update(int virtualPageNumber, int pageFrameNumber) {
        tlbCache.put(virtualPageNumber, pageFrameNumber);
    }

    // 清空TLB
    public void flush() {
        tlbCache.clear();
    }
}