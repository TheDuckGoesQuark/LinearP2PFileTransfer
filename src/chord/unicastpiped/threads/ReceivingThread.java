package chord.unicastpiped.threads;

import chord.unicastpiped.messages.*;
import chord.unicastpiped.node.BlockStore;
import chord.unicastpiped.node.Node;

import java.io.*;
import java.net.Socket;

public class ReceivingThread implements Runnable {

    private Socket socket;
    private BlockStore blockStore; // Handles CRUD operations on file
    private String saveLocation;
    private boolean isRoot;

    private Node node;

    public ReceivingThread(Socket socket, String saveLocation, Node node) throws IOException {
        this.socket = socket;
        this.saveLocation = saveLocation;
    }

    @Override
    public void run() {
        Message message;
        try {
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            do {
                message = (Message) is.readObject();
                chooseAction(message);
            } while (blockStore == null || !blockStore.allFilesReceived());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void chooseAction(Message message) throws IOException, ClassNotFoundException {
        MessageType messageType = message.getMessage();
        if (isRoot && messageType != MessageType.NEW_NODE_MESSAGE) return; // root shouldn't receive file blocks or info about files
        switch (messageType) {
            case NEW_NODE_MESSAGE:
                handleNewNode((ReceivingNodeDetails) Message.deserialize(message.getData()));
                break;
            case FILE_BLOCK_MESSAGE:
                FileBlock fileBlock = (FileBlock) Message.deserialize(message.getData());
                blockStore.writeBlock(fileBlock.getBlockNumber(), fileBlock.getData());
                break;
            case FILE_DETAILS_MESSAGE:
                FileDetails fileDetails = (FileDetails) Message.deserialize(message.getData());
                File file = new File(saveLocation+fileDetails.getFilename());
                this.blockStore = new BlockStore(
                        new RandomAccessFile(file, "rw"),
                        fileDetails.getFileLength()
                );
        }
    }

    void handleNewNode(ReceivingNodeDetails receivingNodeDetails) {
        new GetSenderNodeThread(receivingNodeDetails); // if sending already, pass on message
        // else, this becomes sender
    }
}
