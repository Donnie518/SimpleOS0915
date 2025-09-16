package os.syscall;

public abstract class Syscall {
    // 进程管理
    public int fork() {
        throw new RuntimeException("Not implemented");
    }

    public int exec() {
        throw new RuntimeException("Not implemented");
    }

    public int exit() {
        throw new RuntimeException("Not implemented");
    }

    public int waitFor() {
        throw new RuntimeException("Not implemented");
    }

    // 内存管理
    public int malloc(int size) {
        throw new RuntimeException("Not implemented");
    }

    public int realloc(int p, int size) {
        throw new RuntimeException("Not implemented");
    }

    public int free(int p) {
        throw new RuntimeException("Not implemented");
    }

    // 文件系统
    public int open() {
        throw new RuntimeException("Not implemented");
    }

    public int read() {
        throw new RuntimeException("Not implemented");
    }

    public int write() {
        throw new RuntimeException("Not implemented");
    }

    public int close() {
        throw new RuntimeException("Not implemented");
    }

    // 文件系统-网络通信
    public int socket() {
        throw new RuntimeException("Not implemented");
    }

    public int bind() {
        throw new RuntimeException("Not implemented");
    }

    public int listen() {
        throw new RuntimeException("Not implemented");
    }

    public int accept() {
        throw new RuntimeException("Not implemented");
    }

    public int connect() {
        throw new RuntimeException("Not implemented");
    }

    public int send() {
        throw new RuntimeException("Not implemented");
    }

    public int recv() {
        throw new RuntimeException("Not implemented");
    }

    public int select() {
        throw new RuntimeException("Not implemented");
    }

    public int epoll() {
        throw new RuntimeException("Not implemented");
    }
}