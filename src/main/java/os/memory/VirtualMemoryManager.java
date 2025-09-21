package os.memory;

import hardware.CPU;
import hardware.Disk;
import hardware.Memory;
import os.config.SystemConfig;

import java.util.*;

public class VirtualMemoryManager {

    private static final int pageSize = SystemConfig.PAGE_SIZE;

    private static final BitSet allocatedPhysicalFrames = new BitSet(Memory.getPhysicalMemorySize() / pageSize);
    
    // 页面置换算法 - 简单时钟算法
    private static final Map<Integer, PageFrameInfo> pageFrameTable = new HashMap<>();
    private static int clockHand = 0; // 时钟指针
    
    // 页面置换信息
    private static class PageFrameInfo {
        int pid;                    // 所属进程ID
        int virtualPageNumber;      // 虚拟页号
        boolean referenced;         // 访问位
        boolean modified;            // 修改位
        
        PageFrameInfo(int pid, int virtualPageNumber) {
            this.pid = pid;
            this.virtualPageNumber = virtualPageNumber;
            this.referenced = true;
            this.modified = false;
        }
    }

    /**
     * 分配物理页框
     * @param pageNeeded 需要的页数
     * @return 分配的物理页框号列表，如果分配失败返回null
     */
    private static List<Integer> allocateFrames(int pageNeeded) {
        List<Integer> allocatedFrames = new ArrayList<>();
        int totalFrames = Memory.getPhysicalMemorySize() / pageSize;
        
        // 首先尝试分配空闲页框
        for (int i = 0; i < totalFrames && allocatedFrames.size() < pageNeeded; i++) {
            if (!allocatedPhysicalFrames.get(i)) {
                allocatedPhysicalFrames.set(i);
                allocatedFrames.add(i);
            }
        }
        
        // 如果空闲页框不够，触发页面置换
        while (allocatedFrames.size() < pageNeeded) {
            int evictedFrame = pageReplacement();
            if (evictedFrame == -1) {
                // 页面置换失败，回滚已分配的页框
                for (int frame : allocatedFrames) {
                    allocatedPhysicalFrames.clear(frame);
                }
                return null;
            }
            allocatedFrames.add(evictedFrame);
        }
        
        return allocatedFrames;
    }
    
    /**
     * 页面置换算法 - 时钟算法
     * @return 被置换的页框号，失败返回-1
     */
    private static int pageReplacement() {
        int totalFrames = Memory.getPhysicalMemorySize() / pageSize;
        int startHand = clockHand;
        
        // 扫描一圈寻找可置换的页面
        do {
            PageFrameInfo frameInfo = pageFrameTable.get(clockHand);
            
            if (frameInfo == null) {
                // 该页框没有页面信息，可以直接使用
                return clockHand;
            }
            
            if (!frameInfo.referenced) {
                // 找到可置换的页面
                int evictedFrame = clockHand;
                
                // 如果页面被修改过，需要写回磁盘（模拟）
                if (frameInfo.modified) {
                    // 这里可以添加写回磁盘的逻辑
                    System.out.println("页面 " + frameInfo.virtualPageNumber + " 被修改，写回磁盘");
                }
                
                // 从原进程的页表中移除该页面
                evictPageFromProcess(frameInfo.pid, frameInfo.virtualPageNumber);
                
                // 更新时钟指针
                clockHand = (clockHand + 1) % totalFrames;
                
                return evictedFrame;
            }
            
            // 清除访问位
            frameInfo.referenced = false;
            clockHand = (clockHand + 1) % totalFrames;
            
        } while (clockHand != startHand);
        
        // 如果所有页面都被访问过，强制置换第一个
        PageFrameInfo frameInfo = pageFrameTable.get(startHand);
        if (frameInfo != null) {
            evictPageFromProcess(frameInfo.pid, frameInfo.virtualPageNumber);
        }
        
        clockHand = (startHand + 1) % totalFrames;
        return startHand;
    }
    
    /**
     * 从进程的页表中移除页面
     */
    private static void evictPageFromProcess(int pid, int virtualPageNumber) {
        PageTable pageTable = PageTable.getByPid(pid);
        if (pageTable != null && pageTable.pageTableEntries != null) {
            pageTable.pageTableEntries.removeIf(entry -> entry.virtualPageNumber == virtualPageNumber);
        }
        pageFrameTable.remove(virtualPageNumber);
    }
    
    /**
     * 处理缺页中断
     * @param pid 进程ID
     * @param virtualPageNumber 虚拟页号
     * @return 是否成功处理缺页中断
     */
    public static boolean handlePageFault(int pid, int virtualPageNumber) {
        System.out.println("缺页中断：进程 " + pid + " 请求页面 " + virtualPageNumber);
        
        // 尝试分配一个新的物理页框
        List<Integer> newFrame = allocateFrames(1);
        if (newFrame == null || newFrame.isEmpty()) {
            System.out.println("缺页中断处理失败：无法分配物理页框");
            return false;
        }
        
        int frameNumber = newFrame.get(0);
        
        // 从磁盘加载页面数据（模拟）
        byte[] pageData = loadPageFromDisk(pid, virtualPageNumber);
        
        // 写入物理内存
        MemoryManager.write(frameNumber, pageData);
        
        // 更新页表
        PageTable pageTable = PageTable.getByPid(pid);
        PageTableEntry newEntry = new PageTableEntry();
        newEntry.virtualPageNumber = virtualPageNumber;
        newEntry.pageFrameNumber = frameNumber;
        
        if (pageTable.pageTableEntries == null) {
            pageTable.pageTableEntries = new ArrayList<>();
        }
        pageTable.pageTableEntries.add(newEntry);
        
        // 更新页面帧信息
        pageFrameTable.put(frameNumber, new PageFrameInfo(pid, virtualPageNumber));
        
        System.out.println("缺页中断处理成功：页面 " + virtualPageNumber + " 已加载到物理页框 " + frameNumber);
        return true;
    }
    
    /**
     * 从磁盘加载页面（模拟）
     */
    private static byte[] loadPageFromDisk(int pid, int virtualPageNumber) {
        // 模拟从磁盘加载页面数据
        System.out.println("从磁盘加载进程 " + pid + " 的页面 " + virtualPageNumber);
        return Disk.getVirtualPageData(pid, virtualPageNumber); // 返回空页面数据
    }
    

    
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

        // 获取当前进程的页表
        PageTable pageTable = PageTable.getByPid(pid);
        if (pageTable.pageTableEntries == null) {
            pageTable.pageTableEntries = new ArrayList<>();
        }

        // 为每个页面创建页表项并写入物理内存
        for (int i = 0; i < pageNeeded; i++) {
            int virtualPageNumber = i; // 虚拟页号从0开始
            int physicalFrame = pageFrames.get(i);
            
            // 创建页表项
            PageTableEntry entry = new PageTableEntry();
            entry.virtualPageNumber = virtualPageNumber;
            entry.pageFrameNumber = physicalFrame;
            pageTable.pageTableEntries.add(entry);
            
            // 记录页面帧信息
            pageFrameTable.put(physicalFrame, new PageFrameInfo(pid, virtualPageNumber));
        }
        
        // 使用页表的地址转换功能写入数据
        for (int i = 0; i < pageNeeded; i++) {
            int virtualPageStart = i * pageSize;
            int length = Math.min(bytes.length - virtualPageStart, pageSize);
            byte[] pageData = Arrays.copyOfRange(bytes, virtualPageStart, virtualPageStart + length);
            
            // 使用虚拟地址通过页表转换来写入物理内存
            int virtualAddress = virtualPageStart; // 虚拟地址从0开始
            int physicalAddress = pageTable.getPhysicalAddress(virtualAddress);
            
            // 将数据写入转换后的物理地址
            MemoryManager.write(physicalAddress >> 4, pageData); // 右移4位获取页框号
        }

        return pageTable.pageTableEntries.getFirst().virtualPageNumber;
    }
}
