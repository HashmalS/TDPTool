import java.util.ArrayList;

/**
 * Created on 22.04.2016.
 * @author Sergey Soroka
 */
class Net {
    private String netName;
    ArrayList<Pin> connections;
    int length;

    Net(String name, ArrayList<Pin> cons) {
        netName = name;
        connections = cons;
    }
}
