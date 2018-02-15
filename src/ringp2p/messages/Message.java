package ringp2p.messages;

import java.io.*;

public class Message implements Serializable {
    private MessageType message;
    private byte[] data;

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public Message(MessageType message, Object data) throws IOException {
        this.message = message;
        this.data = serialize(data);
    }

    public MessageType getMessage() {
        return message;
    }

    public byte[] getData() {
        return data;
    }
}
