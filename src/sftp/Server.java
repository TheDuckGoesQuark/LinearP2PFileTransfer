package sftp;

import javax.xml.soap.Node;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        // Set up multicast.server socket
        ServerSocket serverSocket = new ServerSocket(NodeUtil.SERVER_PORT);

        // Wait for accepting multicast.client
        Socket socket = serverSocket.accept();
        System.out.println(InetAddress.getLocalHost().getHostName()+"Accepted connection: " + socket);
        // Retrieve file to send
        File transferFile = new File("helloworld.txt");
        byte[] byteArray = new byte[(int) transferFile.length()];
        FileInputStream fin = new FileInputStream(transferFile);
        BufferedInputStream bin = new BufferedInputStream(fin);
        bin.read(byteArray, 0, byteArray.length);
        // Set up channel of communication with multicast.client
        OutputStream os = socket.getOutputStream();
        // Write data to output stream
        os.write(byteArray, 0, byteArray.length);
        os.flush();
        // Close
        socket.close();
        System.out.println(InetAddress.getLocalHost().getHostName()+"File transfer complete.");

    }
}
