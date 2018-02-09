package sftp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        int fileSize = 2022386; // File size/buffer size
        int bytesRead; // Number of bytes read from input channel
        int currentTot; //

        Socket socket = new Socket(NodeUtil.SERVER_ADDRESS,NodeUtil.SERVER_PORT); // Connects to local ip on same port as server
        byte [] byteArray = new byte [fileSize];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream("/cs/scratch/jm354/copy.txt"); // Save input file to this location
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(byteArray,0,byteArray.length);
        currentTot = bytesRead;

        do {
            bytesRead = is.read(byteArray, currentTot, (byteArray.length-currentTot));
            if(bytesRead >= 0) currentTot += bytesRead;
        } while(bytesRead > -1);

        bos.write(byteArray, 0 , currentTot);
        bos.flush();
        bos.close();
        socket.close();
    }
}
