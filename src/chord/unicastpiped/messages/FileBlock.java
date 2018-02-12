package chord.unicastpiped.messages;

public class FileBlock {
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
