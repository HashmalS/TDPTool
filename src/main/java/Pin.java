/**
 * Created on 01.05.2016.
 * @author HashmalS
 */
abstract class Pin {
    String pinName;
    String direction;
    int pointX;
    int pointY;
    String attachment;

    double load;
    double inDelay;
    double outDelay;

    Pin() {}

    @Override
    public String toString() {
        return attachment + " " + pinName;
    }
}
