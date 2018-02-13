package chord.unicastpiped.node;

import chord.unicastpiped.exceptions.BlockNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
    Each node stores a map of which node it has,
    and the offset of the corresponding block in the file being sent
 **/
public class BlockStore {

    private ConcurrentHashMap<Integer, Long> blockToOffset = new ConcurrentHashMap<>();
    private RandomAccessFile randomAccessFile;

    public BlockStore(RandomAccessFile randomAccessFile, long fileLength) throws IOException {
        this.randomAccessFile = randomAccessFile;
        randomAccessFile.setLength(fileLength);
    }

    public BlockStore() {}

    public synchronized boolean hasBlock(int blockNumber) {
        return blockToOffset.containsKey(blockNumber);
    }

    public void getBlock(int blockNumber, byte[] buffer) throws BlockNotFoundException, IOException {
        if (hasBlock(blockNumber)) {
            long offset = blockToOffset.get(blockNumber);
            randomAccessFile.seek(offset);
            randomAccessFile.readFully(buffer);
        } else throw new BlockNotFoundException();
    }

    public void writeBlock(int blockNumber, byte[] buffer) throws IOException {
        long offset = blockNumber * NodeUtil.FILE_BUFFER_SIZE;
        randomAccessFile.seek(offset);
        randomAccessFile.write(buffer);
        blockToOffset.put(blockNumber, offset);
        blockToOffset.notifyAll();
    }

    public boolean allFilesReceived() throws IOException {
        double expectedNumberOfBlocks = Math.ceil(((double) randomAccessFile.length()/NodeUtil.FILE_BUFFER_SIZE));
        return expectedNumberOfBlocks == blockToOffset.size();
    }
}
