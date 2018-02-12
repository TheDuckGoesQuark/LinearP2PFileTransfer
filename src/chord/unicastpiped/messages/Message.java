package chord.unicastpiped.messages;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType message;
    private Byte[] data;

    public Message(MessageType message, Byte[] data) {
        this.message = message;
        this.data = data;
    }

    public MessageType getMessage() {
        return message;
    }

    public Byte[] getData() {
        return data;
    }

    public void setData(Byte[] data) {
        this.data = data;
    }
}
