package unicastpiped;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NodeUtil {

    // Running on pc5-014-l.cs.st-andrews.ac.uk
    public static final String SERVER_ADDRESS = "138.251.28.4";
    public static final int SERVER_PORT = 15123;
    public static final Map<String,String> NODES;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("pc5-015-l", "138.251.28.6");
        //map.put("pc5-016-l", "138.251.28.8");
        NODES = Collections.unmodifiableMap(map);
    }
    static final String PATH_TO_SCRATCH ="/cs/scratch/jm354/";
}
