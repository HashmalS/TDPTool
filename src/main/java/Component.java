import java.util.*;

/**
 * Created on 21.04.2016.
 * @author Sergey Soroka
 */
class Component {
    String compName;
    String modelName;
    private String type;
    int pointX;
    int pointY;
    double width;
    private char orient;
    ArrayList<ComponentPin> pins = new ArrayList<>();
    List<Pin> inputPins;
    List<Pin> outputPins;

    Component(String name) {
        compName = name;
    }

    Component(String name, String model, String type, int x, int y, char orient) {
        compName = name;
        modelName = model;
        this.type = type;
        pointX = x;
        pointY = y;
        this.orient = orient;
        inputPins = new ArrayList<>();
        outputPins = new ArrayList<>();
    }
    
    void createPinSets() {
        for (Pin p :
                pins) {
            if (Objects.equals(p.direction, "INPUT")) {
                inputPins.add(p);
            }
            else outputPins.add(p);
        }
    }

    void moveTo(int x, int y) {
        pointX = x;
        pointY = y;
    }

    @Override
    public String toString() {
        return String.format("   - %s %s\n      + %s ( %d %d ) %s ;\n",
                compName, modelName, type, pointX, pointY, orient);
    }
}
