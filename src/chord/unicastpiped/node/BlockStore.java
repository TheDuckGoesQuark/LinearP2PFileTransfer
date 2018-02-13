package chord.unicastpiped.node;

import chord.unicastpiped.exceptions.BlockNotFoundException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
    Each node stores a map of which node it has,
    and the offset of the corresponding block in the file being sent
 **/
public class BlockStore {

    private ConcurrentHashMap<Integer, Future<Long>> blockToOffset = new ConcurrentHashMap<>();
    private RandomAccessFile randomAccessFile;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public BlockStore(RandomAccessFile randomAccessFile, long fileLength) throws IOException {
        this.randomAccessFile = randomAccessFile;
        randomAccessFile.setLength(fileLength);
        double expectedNumberOfBlocks = Math.ceil(((double) randomAccessFile.length()/NodeUtil.FILE_BUFFER_SIZE));
    }

    public void getBlock(int blockNumber, byte[] buffer) throws BlockNotFoundException {
        try {
            long offset = (blockToOffset.get(blockNumber)).get();
            randomAccessFile.seek(offset);
            randomAccessFile.readFully(buffer);
        } catch (ExecutionException|InterruptedException|IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBlock(int blockNumber, byte[] buffer) throws IOException {
        long offset = blockNumber * NodeUtil.FILE_BUFFER_SIZE;
        randomAccessFile.seek(offset);
        randomAccessFile.write(buffer);
        Callable<Long> callableLong = () -> offset;
        Future<Long> future = executorService.submit(callableLong);
        blockToOffset.put(blockNumber, future);
    }

    public boolean allFilesReceived() throws IOException {
        double expectedNumberOfBlocks = Math.ceil(((double) randomAccessFile.length()/NodeUtil.FILE_BUFFER_SIZE));
        return expectedNumberOfBlocks == blockToOffset.size();
    }
}
