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

    private String filePath; // Path to file being sent/to save
    private boolean isRoot; // is this node the original host of the file
    private ReceivingNodeDetails receivingNodeDetails; // Details of this node

    public Node(boolean isRoot, String filePath, String node_address) {
        this.isRoot = isRoot;
        this.filePath = filePath;
        this.receivingNodeDetails = new ReceivingNodeDetails(-1);
    }

    public void start() throws IOException, ClassNotFoundException {
        Socket socket = null;
        ClientThread clientThread;
        BlockStore blockStore = new BlockStore();

        if (!isRoot) {
            // Repeat requests until sender found
            while(socket == null) socket = discoverSender();
            // thread for receiving data
            clientThread = new ClientThread(socket, filePath, blockStore);
            clientThread.run();
        } else {
            // init blockstore with file details.
            blockStore = initBlockstore();
        }
        // init thread for sending data
        ServerThread serverThread = new ServerThread(blockStore);
        serverThread.run();
    }

    private Socket discoverSender() {
        Socket socket = null;
        try {
            // Send message to all nodes
            Message message = new Message(MessageType.NEW_NODE_MESSAGE, receivingNodeDetails);
            DatagramSocket s = new DatagramSocket();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(FILE_BUFFER_SIZE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
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
        return new BlockStore(new RandomAccessFile(file,"r"), file.length(), true);
    }
}
