import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 22.04.2016.
 * @author Sergey Soroka
 */
class Net {
    private String netName;
    private ArrayList<Pin> connections;
    public int weight;

    Net(String name, ArrayList<Pin> cons) {
        netName = name;
        connections = cons;
    }

    void showConnections() {
        for (Pin pin: connections) {
                System.out.print(pin.pinName + ", ");
            }
        System.out.println();
    }
}
