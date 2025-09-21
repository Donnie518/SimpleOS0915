package os.memory.strategy;

import hardware.Memory;
import os.memory.VirtualMemoryManager;

import static os.memory.VirtualMemoryManager.*;

public class ClockPageReplacement implements PageReplacementStrategy {

    private static int clockHand = 0; // 时钟指针

    /**
     * 页面置换算法 - 时钟算法
     * @return 被置换的页框号，失败返回-1
     */
    @Override
    public int pageReplacement() {
        int totalFrames = Memory.getPhysicalMemorySize() / pageSize;
        int startHand = clockHand;

        // 扫描一圈寻找可置换的页面
        do {
            VirtualMemoryManager.PageFrameInfo frameInfo = pageFrameTable.get(clockHand);

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
        VirtualMemoryManager.PageFrameInfo frameInfo = pageFrameTable.get(startHand);
        if (frameInfo != null) {
            evictPageFromProcess(frameInfo.pid, frameInfo.virtualPageNumber);
        }

        clockHand = (startHand + 1) % totalFrames;
        return startHand;
    }

}
