package unicastpiped;

import java.io.Serializable;

public class FileInfoMessage implements Serializable {

    private long filelength;
    private String filename;

    public FileInfoMessage(long filelength, String filename) {
        this.filelength = filelength;
        this.filename = filename;
    }

    public long getFilelength() {
        return filelength;
    }

    public void setFilelength(long filelength) {
        this.filelength = filelength;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
