package chord.unicastpiped.threads;

import chord.unicastpiped.messages.Message;
import chord.unicastpiped.node.BlockStore;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static chord.unicastpiped.node.NodeUtil.FILE_BUFFER_SIZE;
import static chord.unicastpiped.node.NodeUtil.MULTICAST_ADDRESS;

/**
 * Listens for new nodes wanting to join the ring.
 * Once found, connects and begins sending file blocks once their available.
 */
public class ServerThread implements Runnable {

    private BlockStore blockStore;

    public ServerThread(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    @Override
    public void run() {
        listenForNewNodes();
    }

    void listenForNewNodes() {
        MulticastSocket socket = null;
        byte[] dataBytes = new byte[FILE_BUFFER_SIZE];
        try {
            InetAddress group = null;
            socket = new MulticastSocket(4446);
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(dataBytes, dataBytes.length);
                socket.receive(datagramPacket);
                byte[] data = datagramPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                try {
                    Message message = (Message) objectInputStream.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
