import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

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

    private ArrayList<ArrayList<Component>> compRows;

    private HashSet<Pin> inputPins;
    private HashSet<Pin> outputPins;

    private DirectedAcyclicGraph<Pin, NetEdge> dag;
    private TopologicalOrderIterator<Pin, NetEdge> iterator, reversedIterator;

    private static final Logger logger = LogManager.getLogger(Program.class.getName());


    Design(String name) {
        designName = name;
        rows = new ArrayList<>();
        components = new ArrayList<>();
        globalPins = new ArrayList<>();
        nets = new ArrayList<>();
        compRows = new ArrayList<>();
        dag = new DirectedAcyclicGraph<>(NetEdge.class);
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

    void checkPaths() {
        setPins();
        AllDirectedPaths<Pin, NetEdge> adp = new AllDirectedPaths<>(dag);
        List<GraphPath<Pin, NetEdge>> paths;
        for (Pin ip :
                inputPins) {
            System.out.println(ip.direction + " pin " + ip.attachment + " " + ip.pinName);
            for (Pin op :
                    outputPins) {
                System.out.println(op.direction + " pin " + op.attachment + " " + op.pinName);
                paths = adp.getAllPaths(ip, op, true, 1000);
                for (GraphPath<Pin, NetEdge> path:
                        paths) {
                    System.out.println(path);
                    System.out.println(path.getWeight() + "\n");
                }
            }
        }
    }

    void createAcyclicGraph() {
        globalPins.forEach(dag::addVertex);
        for (Component comp :
                components) {
            comp.pins.forEach(dag::addVertex);
        }

        for (Net net :
                nets) {
            Pin p1 = net.connections.get(0);
            net.connections.subList(1, net.connections.size()).stream()
                    .filter(p -> dag.containsVertex(p) && dag.containsVertex(p1))
                    .forEach(p -> {
                        NetEdge n = new NetEdge(p1 + " : " + p);
                        n.setLength(abs(p1.pointX - p.pointX + p1.pointY - p.pointY));
                        dag.addEdge(p1, p, n);
                    });
        }
        for (Component comp :
                components) {
            comp.createPinSets();
            for (Pin p :
                    comp.inputPins) {
                for (Pin p1 :
                        comp.outputPins) {
                    try {
                        NetEdge n = new NetEdge(p + " : " + p1);
                        n.setLength(1.0);
                        dag.addEdge(p, p1, n);
                    }
                    catch (IllegalArgumentException ex) {
                        logger.info("Couldn't add edge {" + p.attachment + " " + p.pinName
                                + " : " + p1.attachment + " " + p1.pinName + "}");
                    }
                }
            }
        }
    }

    void topologicalSort() {
        iterator = new TopologicalOrderIterator<>(dag);
        reversedIterator = new TopologicalOrderIterator<>(new EdgeReversedGraph<>(dag));
        while (iterator.hasNext()) {
            Pin p = iterator.next();
            System.out.print(p.attachment + " " + p.pinName + " ");
        }
        System.out.println();
        while (reversedIterator.hasNext()) {
            Pin p = reversedIterator.next();
            System.out.print(p.attachment + " " + p.pinName + " ");
        }
        System.out.println();
    }

    void showLengths() {
        for (NetEdge edge :
                dag.edgeSet()) {
            System.out.println(edge.getName() + ": " + edge.getLength());
        }
    }

    void performSAT() {
        while (iterator.hasNext()) {
            Pin p = iterator.next();
            p.arrivalTime = 0;
            for (DefaultWeightedEdge e :
                    dag.incomingEdgesOf(p)) {
                // calculations
            }
        }
        while (reversedIterator.hasNext()) {
            Pin p = reversedIterator.next();
            p.requiredTime = 0;
            for (DefaultWeightedEdge e :
                    dag.incomingEdgesOf(p)) {
                // calculations
            }
        }
    }

    void componentsToRows() {
        ArrayList<Component> row;
        for (Row r :
                rows) {
            row = new ArrayList<>();
            row.addAll(components.stream().filter(c -> c.pointY == r.origY).collect(Collectors.toList()));
            row.sort((o1, o2) -> {
                if (o1.pointX == o2.pointX) return 0;
                else return o1.pointX > o2.pointX ? 1 : -1;
            });
            compRows.add(row);
        }
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
