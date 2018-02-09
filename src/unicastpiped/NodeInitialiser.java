package unicastpiped;

public class Node {

    public static void main(String[] args) {
        NodeMode nodeMode = getNodeMode();
        Node node = new Node(nodeMode);
    }

    private NodeMode mode;

    Node(NodeMode mode) {
        this.mode = mode;
    }

    private static
}
