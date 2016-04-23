import java.util.ArrayList;

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
    ArrayList<Component> components = new ArrayList<>();
    ArrayList<Pin> pins = new ArrayList<>();
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
        System.out.println("Pins: " + pins.size());
        System.out.println("Nets: " + nets.size());
    }
}