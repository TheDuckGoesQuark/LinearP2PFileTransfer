package ringp2p.messages;

import java.io.Serializable;

public class ReceivingNodeDetails implements Serializable {
    private int last_block_sent;
    private String address;
    private int port;

    public ReceivingNodeDetails(int last_block_sent, String address, int port) {
        this.last_block_sent = last_block_sent;
        this.address = address;
        this.port = port;
    }

    public ReceivingNodeDetails(int last_block_sent) {
        this.last_block_sent = last_block_sent;
    }

    public int getLast_block_sent() {
        return last_block_sent;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "last block received: "+last_block_sent+"\nhost address: "+address+"\nport: "+port;
    }
}
