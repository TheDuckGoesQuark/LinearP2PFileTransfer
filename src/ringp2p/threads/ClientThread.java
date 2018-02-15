package ringp2p.threads;

import ringp2p.messages.FileBlock;
import ringp2p.messages.FileDetails;
import ringp2p.node.BlockStore;
import ringp2p.node.Node;
import ringp2p.messages.Message;
import ringp2p.messages.MessageType;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket socket;
    private String fileLocation;

    /**
     * Constructor for all non-root nodes to receive nodes.
     * @param socket
     * @param saveLocation
     * @throws IOException
     */
    public ClientThread(Socket socket, String saveLocation) throws IOException {
        this.socket = socket;
        this.fileLocation = saveLocation;
    }

    @Override
    public void run() {
        Message message;
        try {
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            do {
                message = (Message) is.readObject();
                chooseAction(message);
            } while (Node.blockStore == null || !Node.blockStore.allFilesReceived());
            System.out.println("All blocks received.");
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failure when trying to retrieve files.");
            e.printStackTrace();
        }
    }

    private void chooseAction(Message message) throws IOException, ClassNotFoundException {
        MessageType messageType = message.getMessage();
        switch (messageType) {
            case FILE_BLOCK_MESSAGE:
                FileBlock fileBlock = (FileBlock) Message.deserialize(message.getData());
                System.out.println("Received file block "+fileBlock.getBlockNumber());
                Node.blockStore.writeBlock(fileBlock.getBlockNumber(), fileBlock.getData());
                break;
            case FILE_DETAILS_MESSAGE:
                FileDetails fileDetails = (FileDetails) Message.deserialize(message.getData());
                System.out.println("Received file details "+fileDetails.getFileLength());
                File file = new File(fileLocation + fileDetails.getFilename());
                synchronized (BlockStore.lock) {
                    Node.blockStore = new BlockStore(
                            new RandomAccessFile(file, "rw"),
                            fileDetails.getFileLength(),
                            false,
                            file
                    );
                    BlockStore.lock.notifyAll();
                }
        }
    }
}
