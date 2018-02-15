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

    private ReceivingNodeDetails receivingNodeDetails;

    public ServerThread() {
    }

    @Override
    public void run() {
        // Gets receiver details
        listenForNewNodes();
        // Wait until blockstore actually has file information
        synchronized (BlockStore.lock) {
            while (!Node.blockStore.isInitialised()) {
                try {
                    System.out.println("Waiting for blockstore to be initialised.");
                    Node.blockStore.wait();
                } catch (Exception e) {
                    System.out.println("Wait for fileblock was interrupted.");
                    System.out.println(e.getMessage());
                }
            }
        }
        // Begin sending file to receiver
        try {
            distributeFile();
        } catch (IOException e) {
            System.out.println("Error when trying to distribute file.");
            e.printStackTrace();
        }
    }

    private void distributeFile() throws IOException {
        // Init socket
        Socket socket = new Socket(receivingNodeDetails.getAddress(), receivingNodeDetails.getPort());
        OutputStream out = socket.getOutputStream();
        // send file meta data
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        Message message = new Message(MessageType.FILE_DETAILS_MESSAGE, new FileDetails(Node.blockStore.getFileLength(), Node.blockStore.getFileName()));
        objectOutputStream.writeObject(message);

        // send the file contents
        for (int i = receivingNodeDetails.getLast_block_sent()+1; i < Node.blockStore.getExpectedNumberOfBlocks(); i++) {
            byte[] file_data = new byte[FILE_BUFFER_SIZE];
            boolean file_retrieved = false;
            while (!file_retrieved) {
                try {
                    Node.blockStore.getBlock(i, file_data);
                    file_retrieved = true;
                } catch (InterruptedException ignored) {}
            }
            Message file_block = new Message(MessageType.FILE_BLOCK_MESSAGE, new FileBlock(i, file_data));
            objectOutputStream.writeObject(file_block);
        }

        socket.shutdownOutput();
        socket.close();
    }

    private void listenForNewNodes() {
        try {
            // init buffers for datagram, socket for connection
            MulticastSocket socket = null;
            InetAddress group = null;
            byte[] dataBytes = new byte[FILE_BUFFER_SIZE];
            socket = new MulticastSocket(MULTICAST_PORT);
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            // Listen for datagram from new node
            DatagramPacket datagramPacket = new DatagramPacket(dataBytes, dataBytes.length);
            socket.receive(datagramPacket);
            byte[] data = datagramPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(in);
            receivingNodeDetails = new ReceivingNodeDetails(
                    dataInputStream.readInt(),
                    datagramPacket.getAddress().toString().replace("/", ""),
                    UNICAST_PORT
            );
            System.out.println("Receiving node: "+ receivingNodeDetails.toString());
        } catch (IOException e) {
            System.out.println("Error when listening for new nodes.");
            e.printStackTrace();
        }
    }
}
