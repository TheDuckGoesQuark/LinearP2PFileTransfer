package ringp2p.threads;

import ringp2p.messages.*;
import ringp2p.node.BlockStore;
import ringp2p.node.Node;

import java.io.*;
import java.net.*;

import static ringp2p.node.NodeUtil.*;

/**
 * Listens for new nodes wanting to join the ring.
 * Once found, connects and begins sending file blocks once their available.
 */
public class ServerThread extends Thread {

    private int leftInChain;

    public ServerThread(int leftInChain) {
        this.leftInChain = leftInChain;
    }

    @Override
    public void run() {
        waitForBlockStore();
        boolean successful = false;
        while (!successful) {
            RequestingNodeDetails requestingNodeDetails = getRequestingNodeDetails();
            if (requestingNodeDetails == null) continue;
            try {
                distributeFile(requestingNodeDetails);
                successful = true;
            } catch (IOException e) {
                System.out.println("Error when trying to distribute file. Trying again");
                e.printStackTrace();
            }
        }
    }

    private void waitForBlockStore() {
        synchronized (BlockStore.lock) {
            while (!Node.blockStore.isInitialised()) {
                try {
                    BlockStore.lock.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void distributeFile(RequestingNodeDetails requestingNodeDetails) throws IOException {
        Socket socket = null;
        try {
            // Init socket
            socket = new Socket(requestingNodeDetails.getAddress(), requestingNodeDetails.getPort());
            OutputStream out = socket.getOutputStream();
            // send file meta data
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            Message chainDetailsMessage = new Message(MessageType.CHAIN_DETAILS_MESSAGE, new ChainDetails(leftInChain));
            Message fileDetailsMessage = new Message(MessageType.FILE_DETAILS_MESSAGE, new FileDetails(Node.blockStore.getFileLength(), Node.blockStore.getFileName()));
            objectOutputStream.writeObject(chainDetailsMessage);
            objectOutputStream.writeObject(fileDetailsMessage);

            // send the file contents
            for (int i = requestingNodeDetails.getLast_block_sent() + 1; i < Node.blockStore.getExpectedNumberOfBlocks(); i++) {
                byte[] file_data = new byte[FILE_BUFFER_SIZE];
                boolean file_retrieved = false;
                while (!file_retrieved) {
                    try {
                        Node.blockStore.getBlock(i, file_data);
                        file_retrieved = true;
                    } catch (InterruptedException ignored) {
                    }
                }
                Message file_block = new Message(MessageType.FILE_BLOCK_MESSAGE, new FileBlock(i, file_data));
                objectOutputStream.writeObject(file_block);
            }
        } finally {
            if (socket != null) {
                socket.shutdownOutput();
                socket.close();
            }
        }
    }

    private RequestingNodeDetails getRequestingNodeDetails() {
        RequestingNodeDetails requestingNodeDetails = null;
        MulticastSocket socket = null;
        InetAddress group;
        byte[] dataBytes = new byte[FILE_BUFFER_SIZE];

        try {
            System.out.println("Listening for connection");
            socket = new MulticastSocket(MULTICAST_PORT);
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            DatagramPacket datagramPacket = new DatagramPacket(dataBytes, dataBytes.length);
            socket.receive(datagramPacket);
            byte[] data = datagramPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            requestingNodeDetails = (RequestingNodeDetails) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error when listening for new nodes.");
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
        return requestingNodeDetails;
    }
}
