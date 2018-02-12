package unicastpiped;

import java.io.IOException;
import java.util.Scanner;

import static unicastpiped.NodeMode.*;

public class NodeInitialiser {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        NodeMode nodeMode = getNodeMode();
        String filePath = null;
        if (nodeMode == DISTRIBUTE) {
            filePath = getFilePath();
        }
        SimpleNode node = new SimpleNode(nodeMode, filePath);
        node.run();
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

    private static String getFilePath() {
        Scanner sc = new Scanner(System.in);
        String input;
        System.out.println("Enter path to file:");
        input = sc.nextLine();
        sc.close();

        return input;
    }
}
