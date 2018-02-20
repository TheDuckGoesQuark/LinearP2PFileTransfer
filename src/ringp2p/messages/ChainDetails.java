package ringp2p.messages;

import java.io.Serializable;

public class ChainDetails implements Serializable {
    private int leftInChain;

    public ChainDetails(int leftInChain) {
        this.leftInChain = leftInChain;
    }

    public int getLeftInChain() {
        return leftInChain;
    }
}
