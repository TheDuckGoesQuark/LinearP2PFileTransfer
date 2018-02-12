package chord.unicastpiped.messages;

import java.io.Serializable;

public class ReceivingNodeDetails implements Serializable {
    private int last_block_sent;
    private String address;

    public ReceivingNodeDetails(int last_block_sent, String address) {
        this.last_block_sent = last_block_sent;
        this.address = address;
    }

    public int getLast_block_sent() {
        return last_block_sent;
    }

    public String getAddress() {
        return address;
    }
}
