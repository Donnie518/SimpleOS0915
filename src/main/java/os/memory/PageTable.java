package os.memory;



import hardware.CPU;

import java.util.List;


public class PageTable {
    private List<PageTableEntry> pageTableEntries;

    private int transToPhysicalAddress(int virtualPageNumber, int offset) {

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

    public int getPhysicalAddress (int virtualAddress) {
        int lastFourBits = virtualAddress & 0b1111;
        int vpn = (virtualAddress >>> 4) & 0b11;
        return transToPhysicalAddress(vpn, lastFourBits);
    }

}

