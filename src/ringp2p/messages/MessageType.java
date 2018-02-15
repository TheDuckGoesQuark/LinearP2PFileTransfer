package ringp2p.messages;

public enum MessageType {
    NEW_NODE_MESSAGE("NEW_NODE"),
    FILE_BLOCK_MESSAGE("FILE_BLOCK"),
    FILE_DETAILS_MESSAGE("FILE_DETAILS"),
    RECEIVER_DETAILS_MESSAGE("RECEIVER_DETAILS"),
    SENDER_DETAILS_MESSAGE("SENDER_DETAILS");

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
