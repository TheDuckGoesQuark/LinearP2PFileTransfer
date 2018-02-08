package multicast.server;

import java.io.*;

public class MulticastServer {

    static final String FILE_NAME = "helloworld.txt";

    public static void main(String[] args) throws IOException {
        TestFileWriter.writeTestFile(FILE_NAME, 10);
        new MulticastServerThread().start();
    }
}
