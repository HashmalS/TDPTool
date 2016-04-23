/**
 * Created on 23.04.2016.
 * @author Sergey Soroka
 */
class Row {
    private String rowName;
    private String siteName;
    private int origX;
    private int origY;
    private char siteOrient;
    private int numX;
    private int numY;
    private int stepX;
    private int stepY;

    Row(String name, String site, int _oX, int _oY, char siteO, int nX, int nY, int sX, int sY) {
        rowName = name;
        siteName = site;
        origX = _oX;
        origY = _oY;
        siteOrient = siteO;
        numX = nX;
        numY = nY;
        stepX = sX;
        stepY = sY;
    }

    @Override
    public String toString() {
        return "ROW " + rowName + " " + siteName + " " + origX + " " + origY + " " + siteOrient +
                " DO " + numX + " BY " + numY + " STEP " + stepX + stepY + " ;";
    }
}
