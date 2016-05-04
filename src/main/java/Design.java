import java.util.ArrayList;
import java.util.List;

/**
 * Created on 23.04.2016.
 * @author HashmalS
 */
class Design {
    private String designName;
    private int[] dieArea1;
    private int[] dieArea2;
    private int dbuPerMicron;

    private int length;
    private int width;

    ArrayList<Row> rows = new ArrayList<>();
    List<Component> components = new ArrayList<>();
    ArrayList<GlobalPin> globalPins = new ArrayList<>();
    ArrayList<Net> nets = new ArrayList<>();

    Design(String name) {
        designName = name;
    }

    Design(String name, int[] areaPoint1, int[] areaPoint2, int dbu) {
        designName = name;
        dieArea1 = areaPoint1;
        dieArea2 = areaPoint2;
        dbuPerMicron = dbu;
        calculateSize();
    }

    String getDesignName() {
        return designName;
    }

    void calculateSize() {
        length = dieArea2[0] - dieArea1[0];
        width = dieArea2[1] - dieArea1[1];
    }

    void setDbuPerMicron(int dbuPerMicron) {
        this.dbuPerMicron = dbuPerMicron;
    }

    void setDieArea2(int[] dieArea2) {
        this.dieArea2 = dieArea2;
    }

    void setDieArea1(int[] dieArea1) {
        this.dieArea1 = dieArea1;
    }

    void showDesignInfo() {
        System.out.println("DBU per micron rate: " + dbuPerMicron);
        System.out.println("Size: " + length + "x" + width);
        System.out.println("Rows: " + rows.size());
        System.out.println("Components: " + components.size());
        System.out.println("Pins: " + globalPins.size());
        System.out.println("Nets: " + nets.size());
    }

    void calculateWireLength() {
        for (Net net :
                nets) {
            int xMin = dieArea2[0];
            int yMin = dieArea2[1];
            int xMax = dieArea1[0];
            int yMax = dieArea1[1];
            for (Pin pin :
                    net.connections) {
                if (pin.pointX > xMax) {
                    xMax = pin.pointX;
                }
                if (pin.pointX < xMin) {
                    xMin = pin.pointX;
                }
                if (pin.pointY > yMax) {
                    yMax = pin.pointY;
                }
                if (pin.pointY < yMin) {
                    yMin = pin.pointY;
                }
            }
            net.length = (xMax - xMin) + (yMax - yMin);
        }
    }
}
