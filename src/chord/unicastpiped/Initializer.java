package chord.unicastpiped;
import chord.unicastpiped.node.Node;
import unicastpiped.NodeMode;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Scanner;

import static unicastpiped.NodeMode.DISTRIBUTE;
import static unicastpiped.NodeMode.LISTENING;

public class Initializer {

    /**
     * Assumes node address is provided in arguments
     * @param args
     */
    public static void main(String[] args) throws UnknownHostException {

        NodeMode nodeMode = getNodeMode();
        String filePath = getFilePath(nodeMode);

        String node_address;
        if (args.length > 0) node_address = args[0];
        else node_address = Inet4Address.getLocalHost().getHostAddress(); // Risky work-around

        Node node = new Node((nodeMode == DISTRIBUTE), filePath, node_address);

        try {
            node.start();
        } catch (IOException e) {
            System.out.println("Failed: "+e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Exiting.");
    }

    private static String getFilePath(NodeMode nodeMode) {
        Scanner sc = new Scanner(System.in);
        String input;
        if (nodeMode == DISTRIBUTE) System.out.println("Enter path to file:");
        if (nodeMode == LISTENING) System.out.println("Enter path to save file:");
        input = sc.nextLine();
        if (input == null || input.equals("")) {
            System.out.println("Defaulting to /cs/scratch/jm354/");
            input = "/cs/scratch/jm354/";
        }
        sc.close();

        return input;
    }

    private static NodeMode getNodeMode() {
        Scanner sc = new Scanner(System.in);
        int input;
        NodeMode nodeMode;
        do {
            System.out.println("Enter number corresponding to node mode:");
            System.out.println(DISTRIBUTE.ordinal() + " - Distribute file");
            System.out.println(LISTENING.ordinal() + " - Listening for files");
            System.out.println("CTRL+C otherwise.");
            input = sc.nextInt();
            if (input == DISTRIBUTE.ordinal()) {
                nodeMode = DISTRIBUTE;
                break;
            }
            if (input == LISTENING.ordinal()) {
                nodeMode = LISTENING;
                break;
            }
        } while (true);
        return nodeMode;
    }

}
