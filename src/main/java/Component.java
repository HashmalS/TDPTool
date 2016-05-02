import java.util.ArrayList;

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
    private char orient;
    ArrayList<ComponentPin> pins = new ArrayList<>();

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
    }

    @Override
    public String toString() {
        return compName + " " + modelName + " " + type + " (" + pointX + ", " + pointY + ") " + orient;
    }
}
