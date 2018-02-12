package chord.unicastpiped.node;

import chord.unicastpiped.exceptions.BlockNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
    Each node stores a map of which node it has,
    and the offset of the corresponding block in the file being sent
 **/
public class BlockStore {

    private HashMap<Integer, Long> blockToOffset = new HashMap<>();
    private RandomAccessFile randomAccessFile;

    public BlockStore(RandomAccessFile randomAccessFile, long fileLength) throws IOException {
        this.randomAccessFile = randomAccessFile;
        randomAccessFile.setLength(fileLength);
    }

    synchronized boolean hasBlock(int blockNumber) {
        return blockToOffset.containsKey(blockNumber);
    }

    synchronized void getBlock(int blockNumber, byte[] buffer) throws BlockNotFoundException, IOException {
        if (hasBlock(blockNumber)) {
            long offset = blockToOffset.get(blockNumber);
            randomAccessFile.seek(offset);
            randomAccessFile.readFully(buffer);
        } else throw new BlockNotFoundException();
    }

    synchronized void writeBlock(int blockNumber, long offset, byte[] buffer) throws IOException {
        randomAccessFile.seek(offset);
        randomAccessFile.write(buffer);
        blockToOffset.put(blockNumber, offset);
    }
}
