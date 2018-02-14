package chord.unicastpiped.messages;

import java.io.Serializable;

public class ReceivingNodeDetails implements Serializable {
    private int last_block_sent;

    public ReceivingNodeDetails(int last_block_sent) {
        this.last_block_sent = last_block_sent;
    }

    public int getLast_block_sent() {
        return last_block_sent;
    }
}
