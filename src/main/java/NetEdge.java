import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by HashmalS on 02.06.2016.
 * @author Sergey Soroka
 */
class NetEdge extends DefaultWeightedEdge{
    private String name;
    private double length;
    private double slack;

    NetEdge(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getSlack() {
        return slack;
    }

    public void setSlack(double slack) {
        this.slack = slack;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Net)) {
            return false;
        }

        Net edge = (Net) obj;
        return name.equals(edge.netName);
    }
}
