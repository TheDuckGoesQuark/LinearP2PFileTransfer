package chord.unicastpiped.node;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Node {

    private BlockStore blockStore; // Stores info about blocks and positions in file
    private boolean isSending; // is node currently sending data
    private boolean isRetrieving; // is node currently retrieving data
    private boolean isRoot; // is this node the original host of the file
    private InputStream inputStream; // handles receiving data from socket
    private OutputStream outputStream; // handles sending data from socket
    private FileOutputStream fileOutputStream; // handles writing file to self

    public Node(boolean isRoot) {
        this.blockStore = new BlockStore();
        this.isSending = false;
        this.isRetrieving = false;
        this.isRoot = isRoot;
    }

    public void run(Boolean isRoot) {
        if(isRoot) {

        }
    }
}
