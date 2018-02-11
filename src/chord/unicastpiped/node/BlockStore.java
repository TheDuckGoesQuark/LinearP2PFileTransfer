package chord.unicastpiped.node;

import chord.unicastpiped.exceptions.BlockNotFoundException;

import java.util.HashMap;

/**
    Each node stores a map of which node it has,
    and the offset of the corresponding block in the file being sent
 **/
public class BlockStore {
    private HashMap<Integer, Integer> blockToOffset = new HashMap<>();

    boolean hasBlock(int blockNumber) {
        return blockToOffset.containsKey(blockNumber);
    }

    int getBlock(int blockNumber) throws BlockNotFoundException {
        if (hasBlock(blockNumber)) return blockToOffset.get(blockNumber);
        else throw new BlockNotFoundException();
    }

    void addBlock(int blockNumber, int block) {
        blockToOffset.put(blockNumber, block);
    }
}
