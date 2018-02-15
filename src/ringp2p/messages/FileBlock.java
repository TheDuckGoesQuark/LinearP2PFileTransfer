package ringp2p.messages;

import java.io.Serializable;

public class FileBlock implements Serializable{
    private int blockNumber;
    private byte[] data;

    public FileBlock(int blockNumber, byte[] data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public byte[] getData() {
        return data;
    }
}
