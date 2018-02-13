package chord.unicastpiped.node;

import chord.unicastpiped.messages.*;
import chord.unicastpiped.threads.ClientThread;
import chord.unicastpiped.threads.ServerThread;
import jdk.nashorn.internal.ir.Block;

import java.io.*;
import java.net.*;

public class Node {

    private String filePath; // Path to file being sent/to save
    private boolean isRoot; // is this node the original host of the file
    private ReceivingNodeDetails receivingNodeDetails; // Details of this node

    public Node(boolean isRoot, String filePath, String node_address) {
        this.isRoot = isRoot;
        this.filePath = filePath;
        this.receivingNodeDetails = new ReceivingNodeDetails(-1, node_address);
    }

    public void start() throws IOException, ClassNotFoundException {
        Socket socket = null;
        ClientThread clientThread;
        BlockStore blockStore = new BlockStore();

        if (!isRoot) {
            // Repeat requests until sender found
            while(socket == null) socket = discoverSender();
            // init thread for receiving data
            clientThread = new ClientThread(socket, filePath, blockStore);
        } else {
            // init blockstore with file details.
            blockStore = initBlockstore();
        }
        // init thread for sending data
        ServerThread serverThread = new ServerThread(blockStore);
    }

    private Socket discoverSender() {
        Socket socket = null;
        try {
            // Send message to all nodes
            DatagramSocket s = new DatagramSocket();
            Message message = new Message(MessageType.NEW_NODE_MESSAGE, receivingNodeDetails);
            byte[] dataBytes = Message.serialize(message);
            DatagramPacket datagramPacket = new DatagramPacket(dataBytes, dataBytes.length, InetAddress.getByName("224.0.0.10"), 4446);
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
        return new BlockStore(new RandomAccessFile(file,"r"), file.length());
    }
}
