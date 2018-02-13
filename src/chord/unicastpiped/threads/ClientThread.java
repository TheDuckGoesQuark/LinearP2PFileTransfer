package chord.unicastpiped.threads;

import chord.unicastpiped.messages.*;
import chord.unicastpiped.node.BlockStore;
import jdk.nashorn.internal.ir.Block;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private BlockStore blockStore; // Handles CRUD operations on file
    private String fileLocation;
    private boolean isRoot;

    /**
     * Constructor for all non-root nodes to receive nodes.
     * @param socket
     * @param saveLocation
     * @throws IOException
     */
    public ClientThread(Socket socket, String saveLocation, BlockStore blockStore) throws IOException {
        this.socket = socket;
        this.fileLocation = saveLocation;
        this.blockStore = blockStore;
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
        switch (messageType) {
            case FILE_BLOCK_MESSAGE:
                FileBlock fileBlock = (FileBlock) Message.deserialize(message.getData());
                blockStore.writeBlock(fileBlock.getBlockNumber(), fileBlock.getData());
                break;
            case FILE_DETAILS_MESSAGE:
                FileDetails fileDetails = (FileDetails) Message.deserialize(message.getData());
                File file = new File(fileLocation + fileDetails.getFilename());
                this.blockStore = new BlockStore(
                        new RandomAccessFile(file, "rw"),
                        fileDetails.getFileLength()
                );
        }
    }
}
