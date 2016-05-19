/**
 * Created on 22.04.2016.
 * @author HashmalS
 */
class GlobalPin extends Pin {
    private String netName;
    private String type;
    private char orient;
    private String layerName;
    private int layerPt1X;
    private int layerPt1Y;
    private int layerPt2X;
    private int layerPt2Y;

    GlobalPin() {}

    GlobalPin(String name, String net, String direct, String type, int ptX, int ptY,
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
        attachment = "PIN";
    }

    String fileWritingString() {
        return "   - " + pinName + " + NET " + netName + "\n" +
                "      + DIRECTION " + direction + "\n" + "      + " + type +
                " ( " + pointX + " " + pointY + " ) " + orient + "\n" +
                "      + LAYER " + layerName + " ( " + layerPt1X + " " +
                layerPt1Y + " ) ( " + layerPt2X + " " + layerPt2Y + " ) ;\n";
    }
}
