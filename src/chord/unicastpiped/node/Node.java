package chord.unicastpiped.node;

import chord.unicastpiped.messages.*;
import chord.unicastpiped.threads.ReceivingThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Node {

    private String filePath; // Path to file being sent/to save
    private boolean isSending; // is node currently sending data
    private boolean isRetrieving; // is node currently retrieving data
    private boolean isRoot; // is this node the original host of the file
    private ObjectInputStream inputStream; // handles receiving data from socket
    private ObjectOutputStream outputStream; // handles sending data from socket
    private FileDetails fileDetails; // stores details about file
    private ServerSocket serverSocket; // For listening to future connections
    private SendingNodeDetails sendingNodeDetails; // Details of 'previous' node in ring
    private ReceivingNodeDetails receivingNodeDetails; // Details of this node

    public Node(boolean isRoot, String filePath, String node_address) {
        this.isSending = false;
        this.isRetrieving = false;
        this.isRoot = isRoot;
        this.filePath = filePath;
        this.receivingNodeDetails = new ReceivingNodeDetails(-1, node_address);
    }

    public void start() throws IOException, ClassNotFoundException {
        // Set up file channel
        File file;
        serverSocket = new ServerSocket(NodeUtil.SERVER_PORT);
        Socket socket;

        if (isRoot) {
            file = new File(this.filePath);
            fileDetails = new FileDetails(file.length(), file.getName());
        } else {
            // Contact static first node in ring
            socket = new Socket(NodeUtil.SERVER_ADDRESS, NodeUtil.SERVER_PORT);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            requestSender(); // Make request to join end of ring
            socket = refreshSender(socket); // Change sender to new sender
            ReceivingThread receivingThread = new ReceivingThread(socket, filePath, this);
        }
    }

    private Socket refreshSender(Socket socket) throws IOException {
        socket.close();
        socket = new Socket(sendingNodeDetails.getAddress(), sendingNodeDetails.getPort());
        inputStream = new ObjectInputStream(socket.getInputStream());
        return socket;
    }

    private void requestSender() throws IOException, ClassNotFoundException {
        Message message = new Message(MessageType.NEW_NODE_MESSAGE, receivingNodeDetails);
        outputStream.writeObject(message);
        message = (Message) inputStream.readObject();
        sendingNodeDetails = (SendingNodeDetails) Message.deserialize(message.getData());
    }

    /*
        Getters and setters
     */

    public BlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public boolean isRetrieving() {
        return isRetrieving;
    }

    public void setRetrieving(boolean retrieving) {
        isRetrieving = retrieving;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getFilepath() {
        return filePath;
    }

    public void setFilepath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public FileDetails getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(FileDetails fileDetails) {
        this.fileDetails = fileDetails;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
