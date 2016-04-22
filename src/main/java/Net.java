import java.util.HashMap;
import java.util.Map;

/**
 * Created on 22.04.2016.
 * @author Sergey Soroka
 */
class Net {
    private String netName;
    private Map<String, String> connections = new HashMap<>();

    Net(String name, Map<String, String> cons) {
        netName = name;
        connections = cons;
    }
}
