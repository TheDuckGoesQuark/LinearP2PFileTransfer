package chord.unicastpiped.threads;

import chord.unicastpiped.node.BlockStore;

public class ServerThread implements Runnable {

    private BlockStore blockStore;

    public ServerThread(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    public void run() {

    }
}
