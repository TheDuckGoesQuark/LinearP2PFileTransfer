package chord.unicastpiped.messages;

import java.io.Serializable;

public class SendingNodeDetails implements Serializable {
    private String address;
    private int port;

    public SendingNodeDetails(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
