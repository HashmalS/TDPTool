import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;
import java.util.stream.Collectors;

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

    private ArrayList<DefaultWeightedEdge> arcs = new ArrayList<>();

    private ArrayList<ArrayList<Component>> compRows;

    private HashSet<Pin> inputPins;
    private HashSet<Pin> outputPins;

    private ListenableDirectedWeightedGraph<Pin, DefaultWeightedEdge> pinDirectedGraph;
    private DirectedAcyclicGraph<Pin, DefaultWeightedEdge> dag;
    private TopologicalOrderIterator<Pin, DefaultWeightedEdge> iterator, reversedIterator;

    private static final Logger logger = LogManager.getLogger(Program.class.getName());


    Design(String name) {
        designName = name;
        rows = new ArrayList<>();
        components = new ArrayList<>();
        globalPins = new ArrayList<>();
        nets = new ArrayList<>();
        compRows = new ArrayList<>();
        pinDirectedGraph = new ListenableDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        dag = new DirectedAcyclicGraph<>(DefaultWeightedEdge.class);
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

        for (Net net :
                nets) {
            Pin p1 = net.connections.get(0);
            net.connections.subList(1, net.connections.size()).stream()
                    .filter(p -> pinGraph.containsVertex(p) && pinGraph.containsVertex(p1))
                    .forEach(p -> pinGraph.addEdge(p1, p));
        }

        for (Component comp :
                components) {
            comp.createPinSets();
            for (Pin p :
                    comp.inputPins) {
                for (Pin p1 :
                        comp.outputPins) {
                    pinGraph.addEdge(p, p1);
                    arcs.add(pinGraph.getEdge(p, p1));
                }
            }
        }

        pinDirectedGraph = pinGraph;
    }

    void updateEdgeLengths() {
        for (Component comp :
                components) {
            comp.createPinSets();
            for (Pin p :
                    comp.inputPins) {
                for (Pin p1 :
                        comp.outputPins) {
                    pinDirectedGraph.setEdgeWeight(pinDirectedGraph.getEdge(p, p1), 1);
                }
            }
        }
        for (Net net :
                nets) {
            Pin p1 = net.connections.get(0);
            net.connections.subList(1, net.connections.size()).stream()
                    .filter(p -> pinDirectedGraph.containsVertex(p) && pinDirectedGraph.containsVertex(p1))
                    .forEach(p -> pinDirectedGraph.setEdgeWeight(pinDirectedGraph.getEdge(p1, p), net.length));
        }
    }

    void checkPaths() {
        setPins();
        AllDirectedPaths<Pin, DefaultWeightedEdge> adp = new AllDirectedPaths<>(pinDirectedGraph);
        List<GraphPath<Pin, DefaultWeightedEdge>> paths;
        for (Pin ip :
                inputPins) {
            System.out.println(ip.direction + " pin " + ip.attachment + " " + ip.pinName);
            for (Pin op :
                    outputPins) {
                System.out.println(op.direction + " pin " + op.attachment + " " + op.pinName);
                paths = adp.getAllPaths(ip, op, true, 1000);
                for (GraphPath<Pin, DefaultWeightedEdge> path:
                        paths) {
                    System.out.println(path);
                    System.out.println(path.getWeight() + "\n");
                }
            }
        }
    }
    
    void createAcyclicGraph() {
        for (Pin p :
                pinDirectedGraph.vertexSet()) {
            dag.addVertex(p);
        }
        for (DefaultWeightedEdge e :
                pinDirectedGraph.edgeSet()) {
            try {
                dag.addEdge(pinDirectedGraph.getEdgeSource(e), pinDirectedGraph.getEdgeTarget(e));
            }
            catch (IllegalArgumentException ex) {
                logger.info("Couldn't add the edge " + e +  " (Cycle founded)");
            }
        }
        /*
        System.out.println(pinDirectedGraph.vertexSet());
        System.out.println(pinDirectedGraph.edgeSet());
        System.out.println(dag.vertexSet());
        System.out.println(dag.edgeSet());
        */
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
