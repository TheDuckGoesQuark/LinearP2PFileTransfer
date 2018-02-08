package sftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        // Set up multicast.server socket
        ServerSocket serverSocket = new ServerSocket(15123);
        // Wait for accepting multicast.client
        Socket socket = serverSocket.accept();
        System.out.println("Accepted connection: " + socket);
        // Retrieve file to send
        File transferFile = new File("helloworld.txt");
        byte[] byteArray = new byte[(int) transferFile.length()];
        FileInputStream fin = new FileInputStream(transferFile);
        BufferedInputStream bin = new BufferedInputStream(fin);
        bin.read(byteArray, 0, byteArray.length);
        // Set up channel of communication with multicast.client
        OutputStream os = socket.getOutputStream();
        System.out.println("Sending Files...");
        // Write data to output stream
        os.write(byteArray, 0, byteArray.length);
        os.flush();
        // Close
        socket.close();
        System.out.println("File transfer complete.");
    }
}
