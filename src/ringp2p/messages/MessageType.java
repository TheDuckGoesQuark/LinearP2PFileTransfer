package ringp2p.messages;

public enum MessageType {
    FILE_BLOCK_MESSAGE("FILE_BLOCK"),
    FILE_DETAILS_MESSAGE("FILE_DETAILS"),
    CHAIN_DETAILS_MESSAGE("CHAIN_DETAILS");

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
