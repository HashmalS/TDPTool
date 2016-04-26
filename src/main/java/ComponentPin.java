/**
 * Created on 26.04.2016.
 * @author Sergey Soroka
 */
class ComponentPin {
    private String pinName;
    private String direction;
    public int pointX;
    public int pointY;

    ComponentPin() {}

    ComponentPin(String name, String direction) {
        pinName = name;
        this.direction = direction;
    }
}
