package chord.unicastpiped.threads;

import chord.unicastpiped.node.BlockStore;

/**
 * Listens for new nodes wanting to join the ring.
 * Once found, connects and begins sending file blocks once their available.
 */
public class ServerThread implements Runnable {

    private BlockStore blockStore;

    public ServerThread(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    public void run() {

    }
}
