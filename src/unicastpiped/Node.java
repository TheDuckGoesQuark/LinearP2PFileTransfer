package unicastpiped;

import sftp.NodeUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
        FileInputStream in = new FileInputStream(file);
        // Init socket
        ServerSocket serverSocket = new ServerSocket(NodeUtil.SERVER_PORT);
        // Wait for accepting client
        Socket socket = serverSocket.accept();
        // Once found...
        OutputStream out = socket.getOutputStream();
        // send file meta data
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        FileInfoMessage fileInfoMessage = new FileInfoMessage(file.length(), fileName);
        System.out.println(fileInfoMessage.getFilelength());
        System.out.println(fileInfoMessage.getFilename());
        objectOutputStream.writeObject(fileInfoMessage);

        // send the file contents
        DataOutputStream dout = new DataOutputStream(out);
        long numSent = 0;
        long numToSend = fileInfoMessage.getFilelength();
        while (numSent < numToSend) {
            long numThisTime = numToSend - numSent;
            numThisTime = numThisTime < bytes.length ? numThisTime : bytes.length;
            int numRead = in.read(bytes, 0, (int) numThisTime);
            if (numRead == -1) break;
            dout.write(bytes, 0, numRead);
            numSent += numRead;
        }
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

        FileOutputStream fos = new FileOutputStream("/cs/scratch/jm354/" + fileInfoMessage.getFilename()); // Save input file to this location
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(byteArray, 0, byteArray.length);
        currentTot = bytesRead;

        do {
            bytesRead = is.read(byteArray, currentTot, (byteArray.length - currentTot));
            if (bytesRead >= 0) currentTot += bytesRead;
        } while (bytesRead > -1);

        bos.write(byteArray, 0, currentTot);
        bos.flush();
        bos.close();
        socket.close();
    }
}
