package hardware;

public class Disk implements IODevice{
    public static byte[] getVirtualPageData (int pid, int vpn) {
        return new byte[4];
    }
}
