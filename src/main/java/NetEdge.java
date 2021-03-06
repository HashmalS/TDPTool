import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Random;

/**
 * Created by HashmalS on 02.06.2016.
 * @author Sergey Soroka
 */
class NetEdge extends DefaultWeightedEdge{
    private String name;
    private double length;
    private double slack;
    private double weight;
    private double delay;

    NetEdge(String name) {
        this.name = name;
        setWeight(1.0);
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    double getLength() {
        return length;
    }

    void setLength(double length) {
        this.length = length;
    }

    double getSlack() {
        return slack;
    }

    void setSlack(double slack) {
        this.slack = slack;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

    double getDelay() {
        return delay;
    }

    void setDelay(double delay) {
        this.delay = delay;
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
