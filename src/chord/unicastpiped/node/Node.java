package chord.unicastpiped.node;

import chord.unicastpiped.messages.*;
import chord.unicastpiped.threads.ClientThread;
import chord.unicastpiped.threads.ServerThread;
import jdk.nashorn.internal.ir.Block;

import java.io.*;
import java.net.*;

import static chord.unicastpiped.node.NodeUtil.FILE_BUFFER_SIZE;
import static chord.unicastpiped.node.NodeUtil.MULTICAST_ADDRESS;

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

    public void start() throws IOException, ClassNotFoundException {
        Socket socket = null;
        ClientThread clientThread;

        if (!isRoot) {
            // Repeat requests until sender found
            while(socket == null) socket = discoverSender();
            // thread for receiving data
            blockStore = new BlockStore();
            clientThread = new ClientThread(socket, filePath);
            clientThread.run();
        } else {
            // init blockstore with file details.
            synchronized (blockStore.lock) {
                blockStore = initBlockstore();
                blockStore.lock.notifyAll();
            }
        }
        // init thread for sending data
        ServerThread serverThread = new ServerThread();
        serverThread.run();
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
            DatagramSocket s = new DatagramSocket();
            byte[] dataBytes = byteArrayOutputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(
                    dataBytes,
                    dataBytes.length,
                    InetAddress.getByName(MULTICAST_ADDRESS),
                    4446);
            s.send(datagramPacket);

            // Listen for join from end of ring
            ServerSocket serverSocket = new ServerSocket(4446);
            socket = serverSocket.accept();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return socket;
    }

    private BlockStore initBlockstore() throws IOException {
        File file = new File(filePath);
        return new BlockStore(new RandomAccessFile(file,"r"), file.length(), true, file);
    }
}
