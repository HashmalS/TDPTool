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

    @Override
    public String toString() {
        String cons = "";
        for (Pin pin :
                connections) {
            cons += " ( " + pin.attachment + " " + pin.pinName +
                    " ) ";
        }
        return "   - " + netName + cons + " ; \n";
    }
}
