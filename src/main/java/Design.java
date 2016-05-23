import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Integer.*;

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

    ArrayList<Row> rows;
    List<Component> components;
    ArrayList<GlobalPin> globalPins;
    ArrayList<Net> nets;

    private HashSet<Pin> inputPins;
    private HashSet<Pin> outputPins;

    private ListenableDirectedWeightedGraph<Pin, DefaultWeightedEdge> pinDirectedGraph;

    Design(String name) {
        designName = name;
        rows = new ArrayList<>();
        components = new ArrayList<>();
        globalPins = new ArrayList<>();
        nets = new ArrayList<>();
        pinDirectedGraph = new ListenableDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    }

    Design(String name, int[] areaPoint1, int[] areaPoint2, int dbu) {
        designName = name;
        dieArea1 = areaPoint1;
        dieArea2 = areaPoint2;
        dbuPerMicron = dbu;
        calculateSize();
        rows = new ArrayList<>();
        components = new ArrayList<>();
        globalPins = new ArrayList<>();
        nets = new ArrayList<>();
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

    void createPinDirectedGraph() {
        ListenableDirectedWeightedGraph<Pin, DefaultWeightedEdge> pinGraph =
                new ListenableDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        globalPins.forEach(pinGraph::addVertex);
        for (Component comp :
                components) {
            comp.pins.forEach(pinGraph::addVertex);
        }

        for (Component comp :
                components) {
            comp.createPinSets();
            for (Pin p :
                    comp.inputPins) {
                for (Pin p1 :
                        comp.outputPins) {
                    pinGraph.addEdge(p1, p);
                    pinGraph.setEdgeWeight(pinGraph.getEdge(p1, p), 1);
                }
            }
        }
        for (Net net :
                nets) {
            Pin p1 = net.connections.get(0);
            net.connections.subList(1, net.connections.size()).stream()
                    .filter(p -> pinGraph.containsVertex(p) && pinGraph.containsVertex(p1))
                    .forEach(p -> {
                        pinGraph.addEdge(p1, p);
                        pinGraph.setEdgeWeight(pinGraph.getEdge(p1, p), net.length);
                    });
        }

        pinDirectedGraph = pinGraph;
    }

    void checkPaths() {
        setPins();
        AllDirectedPaths<Pin, DefaultWeightedEdge> adp = new AllDirectedPaths<>(pinDirectedGraph);
        List<GraphPath<Pin, DefaultWeightedEdge>> paths;
        paths = adp.getAllPaths(inputPins, outputPins, true, MAX_VALUE);
        System.out.println(paths);
    }

    private void setPins() {
        inputPins = new HashSet<>();
        outputPins = new HashSet<>();
        for (Pin p :
                globalPins) {
            if (p.direction.equals("INPUT")) {
                inputPins.add(p);
            }
            else  {
                outputPins.add(p);
            }
        }
    }

    @Override
    public String toString() {
        return "DESIGN " + designName + " ;\n\nUNITS DISTANCE MICRONS " +
                dbuPerMicron + " ;\nDIEAREA ( " + dieArea1[0] + " " + dieArea1[1] +
                " )  ( " + dieArea2[0] + " " + dieArea2[1] + " ) ;\n\n";
    }
}
