package unicastpiped.protocolmessages;

import java.io.Serializable;

public class FileBlock implements Serializable {
    private int size;
    private Byte[] bytes = new Byte[65536];

    public FileBlock(int size, Byte[] bytes) {
        this.size = size;
        this.bytes = bytes;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Byte[] getBytes() {
        return bytes;
    }

    public void setBytes(Byte[] bytes) {
        this.bytes = bytes;
    }
}
