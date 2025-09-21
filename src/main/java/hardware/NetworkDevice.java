package hardware;

import java.nio.*;

public class NetworkDevice implements IODevice {

    private final ByteBuffer receiveBuffer;

    public NetworkDevice() {
        receiveBuffer = ByteBuffer.allocate(1024);
    }

    // 模拟数据到达
    public void onDataReceived(byte[] data) {
        receiveBuffer.put(data);
    }

    // 检查设备是否可读
    public boolean isReadable() {
        return receiveBuffer.position() > 0;
    }
}
