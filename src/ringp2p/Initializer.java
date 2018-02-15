package ringp2p;

import ringp2p.node.Node;
import ringp2p.node.NodeMode;
import ringp2p.node.NodeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Scanner;

import static ringp2p.node.NodeMode.DISTRIBUTE;
import static ringp2p.node.NodeMode.LISTENING;

public class Initializer {

    public static Scanner sc;

    /**
     * Assumes node address is provided in arguments
     *
     * @param args
     */
    public static void main(String[] args) throws UnknownHostException, FileNotFoundException {
        // Workaround for using automated input with intellij
        if (args.length > 0) {
            FileInputStream is = new FileInputStream(new File(args[0]));
            System.setIn(is);
        }
        sc = new Scanner(System.in);

        NodeMode nodeMode = getNodeMode();
        String filePath = getFilePath(nodeMode);
        Node node = new Node((nodeMode == DISTRIBUTE), filePath);

        try {
            System.out.println("Booting up new node.");
            node.start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Exiting.");
    }

    private static String getFilePath(NodeMode nodeMode) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        String input;
        if (nodeMode == DISTRIBUTE) System.out.println("Enter path to file:");
        if (nodeMode == LISTENING) System.out.println("Enter path to save file:");
        input = sc.nextLine();
        if (input == null || input.equals("")) {
            System.out.println("Defaulting to /cs/scratch/jm354/");
            input = NodeUtil.PATH_TO_SCRATCH;
        }
        return input;
    }

    private static NodeMode getNodeMode() {
        int input;
        NodeMode nodeMode;
        do {
            System.out.println("Enter number corresponding to node mode:");
            System.out.println(DISTRIBUTE.ordinal() + " - Distribute file");
            System.out.println(LISTENING.ordinal() + " - Listening for files");
            System.out.println("CTRL+C otherwise.");
            try {
                input = Integer.parseInt(sc.nextLine());
                if (input == DISTRIBUTE.ordinal()) {
                    nodeMode = DISTRIBUTE;
                    break;
                }
                if (input == LISTENING.ordinal()) {
                    nodeMode = LISTENING;
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Must be integer.");
            }
        } while (true);
        return nodeMode;
    }

}
