import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;

/**
 * Created on 22.04.2016.
 * @author Sergey Soroka
 */
class Net {
    String netName;
    ArrayList<Pin> connections;
    int length;

    Net(String name) {
        this.netName = name;
    }

    Net(String name, ArrayList<Pin> cons) {
        netName = name;
        connections = cons;
    }

    String fileWritingString() {
        String cons = "";
        for (Pin pin :
                connections) {
            cons += " ( " + pin.attachment + " " + pin.pinName +
                    " ) ";
        }
        return "   - " + netName + cons + " ; \n";
    }
}
