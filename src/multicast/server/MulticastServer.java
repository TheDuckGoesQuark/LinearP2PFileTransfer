package multicast.server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastServer {

    static final String FILE_NAME = "helloworld.txt";

    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(
            String multicastMessage) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        buf = multicastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        //TestFileWriter.writeTestFile(FILE_NAME, 1000);
        new MulticastServer().multicast("hello world");
    }
}
