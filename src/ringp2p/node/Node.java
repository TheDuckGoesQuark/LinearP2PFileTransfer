package ringp2p.node;

import ringp2p.threads.ClientThread;
import ringp2p.threads.ServerThread;
import ringp2p.messages.RequestingNodeDetails;

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
    private int chainLength;
    private RequestingNodeDetails requestingNodeDetails; // Details of this node

    public Node(boolean isRoot, String filePath) {
        this.isRoot = isRoot;
        this.filePath = filePath;
        this.requestingNodeDetails = new RequestingNodeDetails(-1);
    }

    public Node(boolean isRoot, String filePath, int chainLength) {
        this.filePath = filePath;
        this.isRoot = isRoot;
        this.chainLength = chainLength;
        this.requestingNodeDetails = new RequestingNodeDetails(-1);
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
            this.chainLength = clientThread.getChainLength();
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

        if (chainLength != 0) {
            // init thread for sending data
            serverThread = new ServerThread(chainLength-1);
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
        Enumeration<InetAddress> inetAddress = getValidInterface();
        InetAddress currentAddress;
        currentAddress = inetAddress.nextElement();
        while (inetAddress.hasMoreElements()) {
            currentAddress = inetAddress.nextElement();
            if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
                return currentAddress.toString().replace("/", "");
            }
        }
        return currentAddress.toString().replace("/", "");
    }

    private Enumeration<InetAddress> getValidInterface() throws SocketException {
        final String[] interfaceNames = {"enp2s0", "enp3s0", "enp4s0"};
        int interfaceIndex = 0;
        NetworkInterface networkInterface = null;
        Enumeration<InetAddress> inetAddress = null;
        while (interfaceIndex < interfaceNames.length && networkInterface == null) {
            networkInterface = NetworkInterface.getByName(interfaceNames[interfaceIndex]);
            try {
                inetAddress = networkInterface.getInetAddresses();
            } catch (NullPointerException e) {
                interfaceIndex++;
            }
        }
        if (inetAddress == null) throw new SocketException();
        return inetAddress;
    }

    private Socket discoverSender() {
        Socket socket = null;
        try {
            requestingNodeDetails.setAddress(getHostAddress());
        } catch (SocketException e) {
            System.out.println("Failed to read machines address.");
            return null;
        }
        requestingNodeDetails.setPort(UNICAST_PORT);

        while (socket == null) {
            broadcastDetails();
            try {
                socket = listenForSender(requestingNodeDetails.getPort());
            } catch (IOException e) {
                // Increment until a port is successful
                if (e instanceof SocketTimeoutException) System.out.println("Timed out, trying again...");
                else {
                    System.out.println("Port in use, trying the next one...");
                    System.out.println(e.getMessage());

                    requestingNodeDetails.setPort(requestingNodeDetails.getPort() + 1);
                }
            }
        }

        return socket;
    }

    private Socket listenForSender(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        try {
            System.out.println("Listening for sender...");
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
            objectOutputStream.writeObject(requestingNodeDetails);
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
