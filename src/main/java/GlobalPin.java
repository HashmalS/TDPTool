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

    @Override
    public String toString() {
        return new StringBuilder().append("   - ").append(pinName).append(" + NET ").append(netName).append("\n")
                .append("      + DIRECTION ").append(direction).append("\n").append("      + ").append(type)
                .append(" ( ").append(pointX).append(" ").append(pointY).append(" ) ").append(orient).append("\n")
                .append("      + LAYER ").append(layerName).append(" ( ").append(layerPt1X).append(" ")
                .append(layerPt1Y).append(" ) ( ").append(layerPt2X).append(" ").append(layerPt2Y).append(" ) ;\n")
                .toString();
    }
}
