package os.memory;

import hardware.CPU;
import java.util.*;

public class PageTable {
    public List<PageTableEntry> pageTableEntries;

    private int transToPhysicalAddress(int virtualPageNumber, int offset) {
        // TLB 看看有无缓存命中
        if (CPU.TLB.lookup(virtualPageNumber) != null) {
            return CPU.TLB.lookup(virtualPageNumber);
        }

        // 查找页表项
        PageTableEntry entry = pageTableEntries.stream()
                .filter(p -> p.virtualPageNumber == virtualPageNumber)
                .findFirst()
                .orElse(null);

        if (entry == null) {
            // 页表项不存在，触发缺页中断
            boolean pageFaultHandled = VirtualMemoryManager.handlePageFault(getCurrentPid(), virtualPageNumber);
            if (!pageFaultHandled) {
                throw new RuntimeException("缺页中断处理失败");
            }
            // 重新尝试获取页表项
            return transToPhysicalAddress(virtualPageNumber, offset);
        }

        int pageFrameNumber = entry.pageFrameNumber;
        CPU.TLB.update(virtualPageNumber, pageFrameNumber);
        return (pageFrameNumber << 4) | (offset & 0b1111);
    }

    public int getPhysicalAddress(int virtualAddress) {
        int lastFourBits = virtualAddress & 0b1111;
        int vpn = (virtualAddress >>> 4) & 0b11;
        return transToPhysicalAddress(vpn, lastFourBits);
    }

    private static final Map<Integer, PageTable> pageTables = new HashMap<>();
    private static int currentPid = 0; // 当前进程ID

    public static PageTable getByPid(int pid) {
        if (!pageTables.containsKey(pid)) {
            PageTable newPageTable = new PageTable();
            newPageTable.pageTableEntries = new ArrayList<>();
            pageTables.put(pid, newPageTable);
        }
        currentPid = pid;
        return pageTables.get(pid);
    }
    
    private static int getCurrentPid() {
        return currentPid;
    }

}

