/**
 * Created on 26.04.2016.
 * @author Sergey Soroka
 */
class ComponentPin extends Pin {
    private Component component;

    ComponentPin() {}

    ComponentPin(String name, String direction, Component component) {
        pinName = name;
        this.direction = direction;
        pointX = component.pointX;
        pointY = component.pointY;
        this.component = component;
    }
}
