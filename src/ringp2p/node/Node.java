package ringp2p.node;

import ringp2p.Initializer;
import ringp2p.threads.ClientThread;
import ringp2p.threads.ServerThread;
import ringp2p.messages.ReceivingNodeDetails;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

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
            if ((socket = discoverSender()) == null) return;

            // thread for receiving data
            clientThread = new ClientThread(socket, filePath);
            clientThread.start();
        } else {
            try {
                synchronized (BlockStore.lock) {
                    blockStore = initBlockstore();
                    BlockStore.lock.notifyAll();
                }
            } catch (IOException e) {
                System.out.println("Failed to retrieve file to distribute.");
                e.printStackTrace();
                return;
            }
        }

        // init thread for sending data
        if (!Initializer.isLast) {
            serverThread = new ServerThread();
            serverThread.start();
        }

        // end once all operations complete
        try {
            if (clientThread != null) {
                clientThread.join();
                socket.close();
            }
            if (serverThread != null) serverThread.join();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Exiting.");
    }

    private String getHostAddress() throws SocketException {
        String interfaceName = "enp2s0";
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
        InetAddress currentAddress;
        currentAddress = inetAddress.nextElement();
        while(inetAddress.hasMoreElements()) {
            currentAddress = inetAddress.nextElement();
            if(currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
                return currentAddress.toString().replace("/", "");
            }
        }
        return currentAddress.toString().replace("/", "");
    }

    private Socket discoverSender() {
        Socket socket = null;
        try {
            receivingNodeDetails.setAddress(getHostAddress());
        } catch (SocketException e) {
            System.out.println("Failed to read machines address.");
            return null;
        }
        receivingNodeDetails.setPort(UNICAST_PORT);

        while (socket == null) {
            broadcastDetails();
            try {
                socket = listenForSender(receivingNodeDetails.getPort());
            } catch (IOException e) {
                // Increment until a port is successful
                if (e instanceof SocketTimeoutException) System.out.println("Timed out, trying again...");
                else {
                    System.out.println("Port in use, trying the next one...");
                    System.out.println(e.getMessage());

                    receivingNodeDetails.setPort(receivingNodeDetails.getPort() + 1);
                }
            }
        }

        return socket;
    }

    private Socket listenForSender(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            serverSocket.close();
        }
        return null;
    }

    private void broadcastDetails() {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        DatagramSocket socket = null;

        try {
            // Convert info to bytes
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(receivingNodeDetails);
            // Send request to all nodes
            socket = new DatagramSocket(MULTICAST_PORT);
            byte[] dataBytes = byteArrayOutputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(
                    dataBytes,
                    dataBytes.length,
                    InetAddress.getByName(MULTICAST_ADDRESS),
                    MULTICAST_PORT);
            socket.send(datagramPacket);
        } catch (IOException ignored) {
        } finally {
            if (socket != null) socket.close();
        }
    }

    private BlockStore initBlockstore() throws IOException {
        File file = new File(filePath);
        return new BlockStore(new RandomAccessFile(file, "r"), file.length(), true, file);
    }
}
