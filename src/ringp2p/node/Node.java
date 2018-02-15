package ringp2p.node;

import ringp2p.Initializer;
import ringp2p.threads.ClientThread;
import ringp2p.threads.ServerThread;
import ringp2p.messages.ReceivingNodeDetails;

import java.io.*;
import java.net.*;

import static ringp2p.node.NodeUtil.MULTICAST_ADDRESS;
import static ringp2p.node.NodeUtil.MULTICAST_PORT;
import static ringp2p.node.NodeUtil.UNICAST_PORT;

public class Node {

    public static BlockStore blockStore;

    private String filePath; // Path to file being sent/to save
    private boolean isRoot; // is this node the original host of the file
    private ReceivingNodeDetails receivingNodeDetails; // Details of this node

    public Node(boolean isRoot, String filePath) {
        this.isRoot = isRoot;
        this.filePath = filePath;
        this.receivingNodeDetails = new ReceivingNodeDetails(-1);
    }

    public void start() throws ClassNotFoundException {
        Socket socket = null;
        ClientThread clientThread = null;
        ServerThread serverThread = null;
        blockStore = new BlockStore();

        if (!isRoot) {
            // Repeat requests until sender found
            while (socket == null) {
                socket = discoverSender();
            }
            // thread for receiving data
            try {
                clientThread = new ClientThread(socket, filePath);
                clientThread.start();
            } catch (IOException e) {
                System.out.println("Failure when running client thread.");
                System.out.println(e.getMessage());
            }
        } else {
            // init blockstore with file details.
            synchronized (BlockStore.lock) {
                try {
                    blockStore = initBlockstore();
                    BlockStore.lock.notifyAll();
                } catch (IOException e) {
                    System.out.println("Failure when initialising blockstore.");
                    System.out.println(e.getMessage());
                }
            }
        }
        // init thread for sending data
        if (Initializer.isLast) {
            serverThread = new ServerThread();
            serverThread.start();
        }

        // end once all operations complete
        try {
            if (clientThread != null) clientThread.join();
            if (serverThread != null) serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Socket discoverSender() {
        Socket socket = null;
        try {
            // Convert info to bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(receivingNodeDetails.getLast_block_sent());
            dataOutputStream.close();
            // Send request to all nodes
            DatagramSocket s = new DatagramSocket(MULTICAST_PORT);
            byte[] dataBytes = byteArrayOutputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(
                    dataBytes,
                    dataBytes.length,
                    InetAddress.getByName(MULTICAST_ADDRESS),
                    MULTICAST_PORT);
            s.send(datagramPacket);
            s.close();

            // Listen for join from end of ring
            ServerSocket serverSocket = new ServerSocket(UNICAST_PORT);
            socket = serverSocket.accept();
        } catch (Exception e) {
            System.out.println("Failure when trying to join ring.");
            System.out.println(e.getMessage());
        }
        return socket;
    }

    private BlockStore initBlockstore() throws IOException {
        File file = new File(filePath);
        return new BlockStore(new RandomAccessFile(file, "r"), file.length(), true, file);
    }
}
