package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestFileWriter {
    public static void writeTestFile(String filename, int lineCount) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < lineCount; i++) {
            writer.write(i + "\n");
        }
        writer.close();
    }
}
