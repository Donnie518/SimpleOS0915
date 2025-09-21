package os.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VirtualMemoryManagerTest {
    

    @Test
    void testAllocateMemoryWithPageFault() {
        // 测试内存分配和缺页中断处理
        int pid = 1;
        byte[] testData = new byte[4096]; // 4KB数据，需要4个页面
        
        // 填充测试数据
        for (int i = 0; i < testData.length; i++) {
            testData[i] = (byte) (i % 256);
        }
        
        // 分配内存
        int virtualAddress = VirtualMemoryManager.allocate(pid, testData);
        
        // 验证分配成功
        assertNotEquals(-1, virtualAddress, "内存分配应该成功");
        
        // 验证页表已创建
        PageTable pageTable = PageTable.getByPid(pid);
        assertNotNull(pageTable, "页表应该已创建");
        assertNotNull(pageTable.pageTableEntries, "页表项列表应该已初始化");
        assertEquals(4, pageTable.pageTableEntries.size(), "应该创建了4个页表项");
        
        System.out.println("内存分配测试通过");
    }
    
    @Test
    void testPageFaultHandling() {
        // 测试缺页中断处理
        int pid = 2;
        int virtualPageNumber = 0;
        
        // 直接触发缺页中断处理
        boolean result = VirtualMemoryManager.handlePageFault(pid, virtualPageNumber);
        
        assertTrue(result, "缺页中断处理应该成功");
        
        // 验证页表已更新
        PageTable pageTable = PageTable.getByPid(pid);
        assertNotNull(pageTable, "页表应该已创建");
        
        boolean pageExists = pageTable.pageTableEntries.stream()
                .anyMatch(entry -> entry.virtualPageNumber == virtualPageNumber);
        assertTrue(pageExists, "页表项应该已添加");
        
        System.out.println("缺页中断处理测试通过");
    }
    
    @Test
    void testMultipleProcessMemoryAllocation() {
        // 测试多进程内存分配
        int pid1 = 1;
        int pid2 = 2;
        byte[] data1 = new byte[2048]; // 2KB
        byte[] data2 = new byte[2048]; // 2KB
        
        // 为两个进程分配内存
        int addr1 = VirtualMemoryManager.allocate(pid1, data1);
        int addr2 = VirtualMemoryManager.allocate(pid2, data2);
        
        assertNotEquals(-1, addr1, "进程1内存分配应该成功");
        assertNotEquals(-1, addr2, "进程2内存分配应该成功");
        
        // 验证两个进程有独立的页表
        PageTable pageTable1 = PageTable.getByPid(pid1);
        PageTable pageTable2 = PageTable.getByPid(pid2);
        
        assertNotNull(pageTable1, "进程1页表应该存在");
        assertNotNull(pageTable2, "进程2页表应该存在");
        assertNotEquals(pageTable1, pageTable2, "两个进程应该有独立的页表");
        
        System.out.println("多进程内存分配测试通过");
    }
}