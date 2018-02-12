package chord.unicastpiped.messages;

public enum MessageType {
    NEW_NODE_MESSAGE("NEW_NODE"),
    FILE_BLOCK("FILE_BLOCK"),
    RECEIVER_DETAILS("RECEIVER_DETAILS");

    private String type;

    MessageType(String type) {
        this.type = type;
    }

    boolean equals(MessageType type) {
        return this.type.equals(type.toString());
    }

    public String toString() {
        return this.type;
    }
}
