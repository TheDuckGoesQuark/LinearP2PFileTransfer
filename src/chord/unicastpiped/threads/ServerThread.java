package chord.unicastpiped.threads;

import chord.unicastpiped.messages.*;
import chord.unicastpiped.node.Node;

import java.io.*;
import java.net.*;

import static chord.unicastpiped.node.NodeUtil.FILE_BUFFER_SIZE;
import static chord.unicastpiped.node.NodeUtil.MULTICAST_ADDRESS;

/**
 * Listens for new nodes wanting to join the ring.
 * Once found, connects and begins sending file blocks once their available.
 */
public class ServerThread implements Runnable {

    private ReceivingNodeDetails receivingNodeDetails;

    public ServerThread() {
    }

    @Override
    public void run() {
        // Gets receiver details
        listenForNewNodes();
        // Wait until blockstore actually has file information
        synchronized (Node.blockStore.lock) {
            while (!Node.blockStore.isInitialised()) {
                try {
                    Node.blockStore.wait();
                } catch (Exception ignored) {
                }
            }
        }
        // Begin sending file to receiver
        try {
            distributeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void distributeFile() throws IOException {
        // Init socket
        Socket socket = new Socket(receivingNodeDetails.getAddress(), receivingNodeDetails.getPort());
        // Wait for accepting client
        OutputStream out = socket.getOutputStream();
        // send file meta data
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        Message message = new Message(MessageType.FILE_DETAILS_MESSAGE, new FileDetails(Node.blockStore.getFileLength(), Node.blockStore.getFileName()));
        objectOutputStream.writeObject(message);

        // send the file contents
        for (int i = 0; i < Node.blockStore.getExpectedNumberOfBlocks(); i++) {
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
            socket = new MulticastSocket(4446);
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
                    datagramPacket.getAddress().toString(),
                    datagramPacket.getPort()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
