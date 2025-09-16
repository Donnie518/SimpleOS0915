package org.example.syscall;

import org.example.ProcessControlBlock;

import java.io.*;


public class MemorySyscall extends Syscall{


    /**
     * 进程创建
     * @return 进程号 pid
     */
    public int exec(String path){
        // 用 Java 读文件的方式替代
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);

            ProcessControlBlock processControlBlock = ProcessControlBlock.createProcess(data);
        } catch (Exception e) {
            return -1;
        }

    }

    public static void main(String[] args) {

    }

    /**
     * 进程中止
     * @return
     */
    public int exit() {
        throw new RuntimeException("Not implemented");
    }


    @Override
    public int malloc(int size) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int realloc(int p, int size) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int free(int p) {
        throw new RuntimeException("Not implemented");
    }
}
