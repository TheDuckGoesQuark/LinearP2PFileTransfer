package chord.unicastpiped.node;

import chord.unicastpiped.messages.FileDetails;

import java.io.*;
import java.net.Socket;

public class Node {

    private BlockStore blockStore; // Handles CRUD operations on file
    private String filePath; // Path to file being sent/to save
    private boolean isSending; // is node currently sending data
    private boolean isRetrieving; // is node currently retrieving data
    private boolean isRoot; // is this node the original host of the file
    private ObjectInputStream inputStream; // handles receiving data from socket
    private ObjectOutputStream outputStream; // handles sending data from socket
    private FileDetails fileDetails; // stores details about file

    public Node(boolean isRoot, String filePath) {
        this.isSending = false;
        this.isRetrieving = false;
        this.isRoot = isRoot;
        this.filePath = filePath;
    }

    public void start() throws IOException, ClassNotFoundException {
        // Set up file channel
        File file;
        if (isRoot) {
            file = new File(this.filePath);
            fileDetails = new FileDetails(file.length(), file.getName());
        } else {
            Socket socket = new Socket(NodeUtil.SERVER_ADDRESS, NodeUtil.SERVER_PORT);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            fileDetails = (FileDetails) inputStream.readObject();
            filePath += fileDetails.getFilename();
            file = new File(this.filePath);
        }

        this.blockStore = new BlockStore(
                new RandomAccessFile(file, "rw"),
                fileDetails.getFileLength()
        );

        listenForMessage();
    }

    void getSenderDetails() {

    }

    public void listenForMessage() {

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

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public void setFileOutputStream(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public String getFilepath() {
        return filePath;
    }

    public void setFilepath(String filePath) {
        this.filePath = filePath;
    }
}
