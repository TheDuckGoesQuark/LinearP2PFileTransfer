package ringp2p.threads;

import ringp2p.messages.*;
import ringp2p.node.BlockStore;
import ringp2p.node.Node;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket socket;
    private String fileLocation;
    private ObjectInputStream is;

    /**
     * Constructor for all non-root nodes to receive file blocks.
     *
     * @param socket
     * @param saveLocation
     */
    public ClientThread(Socket socket, String saveLocation) {
        this.socket = socket;
        this.fileLocation = saveLocation;
    }

    public int getChainLength() {
        try {
            Message message;
            is = new ObjectInputStream(socket.getInputStream());
            message = (Message) is.readObject();
            return ((ChainDetails) Message.deserialize(message.getData())).getLeftInChain();
        } catch (IOException | ClassNotFoundException e) {
            try {
                socket.close();
            } catch (IOException f) {
                f.printStackTrace();
            }
        }

        return 0;
    }

    @Override
    public void run() {
        Message message;
        try {
            if (is == null) is = new ObjectInputStream(socket.getInputStream());
            do {
                message = (Message) is.readObject();
                chooseAction(message);
            } while (Node.blockStore == null || !Node.blockStore.allFilesReceived());
            System.out.println("All blocks received.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failure when trying to retrieve files.");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void chooseAction(Message message) throws IOException, ClassNotFoundException {
        MessageType messageType = message.getMessage();
        switch (messageType) {
            case FILE_BLOCK_MESSAGE:
                FileBlock fileBlock = (FileBlock) Message.deserialize(message.getData());
                Node.blockStore.writeBlock(fileBlock.getBlockNumber(), fileBlock.getData());
                break;
            case FILE_DETAILS_MESSAGE:
                FileDetails fileDetails = (FileDetails) Message.deserialize(message.getData());
                File file = new File(fileLocation + fileDetails.getFilename());
                if (file.exists()) file.delete(); // Deletes file to avoid old file length being read
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
