package ringp2p;

import ringp2p.node.Node;
import ringp2p.node.NodeMode;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

import static ringp2p.node.NodeMode.DISTRIBUTE;
import static ringp2p.node.NodeMode.LISTENING;

public class Initializer {

    /**
     * Usage ./Initializer {"LISTENING"|"DISTRIBUTE"} {"savelocation"} {chainlength}
     * Chain length only used when distributing
     * @param args
     */
    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
        if (args.length < 2) {
            System.out.println("Usage: ./Initializer {\"listening\"/\"distribute\"} {\"savelocation\"}");
            return;
        }

        NodeMode nodeMode = getNodeMode(args[0]);
        String filePath = args[1];
        Node node;

        if (nodeMode == DISTRIBUTE) {
            if (args.length >= 3) {
                node = new Node(nodeMode == DISTRIBUTE, filePath, Integer.parseInt(args[2]));
            } else {
                node = new Node(nodeMode == DISTRIBUTE, filePath, 3); // default 3
            }
        } else node = new Node(nodeMode == DISTRIBUTE, filePath);

        try {
            node.start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static NodeMode getNodeMode(String input) {
        if (input.equals(NodeMode.DISTRIBUTE.name())) return DISTRIBUTE;
        else return LISTENING;
    }

}
