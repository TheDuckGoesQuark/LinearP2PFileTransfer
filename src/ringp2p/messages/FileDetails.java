package ringp2p.messages;

import java.io.Serializable;

public class FileDetails implements Serializable {
    private long fileLength;
    private String filename;

    public FileDetails(long fileLength, String filename) {
        this.fileLength = fileLength;
        this.filename = filename;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
