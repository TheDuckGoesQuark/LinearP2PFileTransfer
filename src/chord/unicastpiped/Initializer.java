package chord.unicastpiped;
import chord.unicastpiped.node.Node;
import unicastpiped.NodeMode;

import java.io.IOException;
import java.util.Scanner;

import static unicastpiped.NodeMode.DISTRIBUTE;
import static unicastpiped.NodeMode.LISTENING;

public class Initializer {

    public static void main(String[] args) {

        NodeMode nodeMode = getNodeMode();
        String filePath = getFilePath(nodeMode);

        Node node = new Node((nodeMode == DISTRIBUTE), filePath);

        try {
            node.start();
        } catch (IOException e) {
            System.out.println("Failed: "+e.getMessage());
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
