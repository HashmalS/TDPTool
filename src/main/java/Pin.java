/**
 * Created on 22.04.2016.
 * @author HashmalS
 */
class Pin {
    private String pinName;
    private String netName;
    private String direction;
    private String type;
    private int pointX;
    private int pointY;
    private char orient;
    private String layerName;
    private int layerPt1X;
    private int layerPt1Y;
    private int layerPt2X;
    private int layerPt2Y;

    Pin() {}

    Pin(String name, String net, String direct, String type, int ptX, int ptY,
        char o, String layer, int pt1X, int pt1Y, int pt2X, int pt2Y) {
        pinName = name;
        netName = net;
        direction = direct;
        this.type = type;
        pointX = ptX;
        pointY = ptY;
        orient = o;
        layerName = layer;
        layerPt1X = pt1X;
        layerPt1Y = pt1Y;
        layerPt2X = pt2X;
        layerPt2Y = pt2Y;
    }

    @Override
    public String toString() {
        return "   - " + pinName + " + NET " + netName + "\n" +
                "      + DIRECTION " + direction + "\n" +
                "      + " + type + " ( " + pointX + " " + pointY + " ) " + orient + "\n" +
                "\t\t\t+ LAYER " + layerName + " ( " + layerPt1X + " " + layerPt1Y +
                " ) ( " + layerPt2X + " " + layerPt2Y + " ) ;";
    }
}
