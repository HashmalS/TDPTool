/**
 * Created on 21.04.2016.
 * @author Sergey Soroka
 */
class Component {
    private String compName;
    private String modelName;
    private String type;
    private int ptX;
    private int ptY;
    private char orient;

    Component(String name, String model, String type, int x, int y, char orient) {
        compName = name;
        modelName = model;
        this.type = type;
        ptX = x;
        ptY = y;
        this.orient = orient;
    }

    @Override
    public String toString() {
        return compName + " " + modelName + " " + type + " (" + ptX + ", " + ptY + ") " + orient;
    }
}
