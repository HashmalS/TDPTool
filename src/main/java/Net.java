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
            cons += new StringBuilder().append(" ( ").append(pin.attachment).append(" ").append(pin.pinName)
                    .append(" ) ").toString();
        }
        return new StringBuilder().append("   - ").append(netName).append(cons).append(" ; \n").toString();
    }
}
