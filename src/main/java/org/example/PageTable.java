package org.example;



import org.example.hardware.CPU;

import java.util.List;


public class PageTable {
    private List<PageTableEntry> pageTableEntries;

    private int getVirtualAddress (int virtualPageNumber, int offset) {

        // TLB 看看有无缓存命中
        if (CPU.TLB.lookup(virtualPageNumber) != null) {
            return CPU.TLB.lookup(virtualPageNumber);
        }

        // RISC 指令集就是在这里写 这个时间复杂度很大，并且还需要一个额外的内存引用
        int pageFrameNumber = this.pageTableEntries.stream().filter(
                p -> p.virtualPageNumber == virtualPageNumber
        ).findFirst().get().pageFrameNumber;

        CPU.TLB.update(virtualPageNumber, pageFrameNumber);
        return (pageFrameNumber << 4) | (offset & 0b1111);
    }

    public int getVirtualAddress (int address) {
        int lastFourBits = address & 0b1111;
        int vpn = (address >>> 4) & 0b11;
        return getVirtualAddress(vpn, lastFourBits);
    }

}

class PageTableEntry {
    int virtualPageNumber;

    int pageFrameNumber;
}
