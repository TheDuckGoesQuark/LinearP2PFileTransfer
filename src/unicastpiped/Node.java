package unicastpiped;

import sftp.NodeUtil;
import unicastpiped.protocolmessages.FileInfoMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static unicastpiped.NodeUtil.PATH_TO_SCRATCH;

public class Node {
    private NodeMode mode;
    private String fileName;

    Node(NodeMode mode, String fileName) {
        this.mode = mode;
        this.fileName = fileName;
    }

    void run() throws IOException, ClassNotFoundException {
        switch (mode) {
            case LISTENING:
                this.listenForFile();
                break;
            case DISTRIBUTE:
                this.distributeFile();
                break;
        }
    }

    private void distributeFile() throws IOException {
        // Retrieve file
        byte[] bytes = new byte[65536];
        File file = new File(this.fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        // Init socket
        ServerSocket serverSocket = new ServerSocket(NodeUtil.SERVER_PORT);
        // Wait for accepting client
        Socket socket = serverSocket.accept();
        OutputStream out = socket.getOutputStream();
        // send file meta data
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        FileInfoMessage fileInfoMessage = new FileInfoMessage(file.length(), fileName);
        objectOutputStream.writeObject(fileInfoMessage);
        objectOutputStream.flush();

        // send the file contents
        DataOutputStream dout = new DataOutputStream(out);
        int numSent = 0;

        while ((numSent = fileInputStream.read(bytes)) != -1) {
            out.write(bytes, 0, numSent);
        }

        socket.shutdownOutput();
        socket.close();

    }

    private void listenForFile() throws IOException, ClassNotFoundException {
        int bytesRead; // Number of bytes read from input channel
        int currentTot; //
        byte[] byteArray = new byte[65536]; // buffer

        Socket socket = new Socket(NodeUtil.SERVER_ADDRESS, NodeUtil.SERVER_PORT); // Connects to local ip on same port as server
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        FileInfoMessage fileInfoMessage = (FileInfoMessage) objectInputStream.readObject();
        FileOutputStream fos = new FileOutputStream(PATH_TO_SCRATCH + fileInfoMessage.getFilename()); // Save input file to this location
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        currentTot = 0;

        while ((bytesRead = is.read(byteArray, 0, byteArray.length)) != -1) {
            if (bytesRead >= 0) currentTot += bytesRead;
            System.out.println(bytesRead);
            bos.write(byteArray, 0, bytesRead);
            bos.flush();
        }

        bos.close();
        socket.close();
    }
}
