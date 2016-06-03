import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.EdgeReversedGraph;
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

    private ArrayList<ArrayList<Component>> compRows;

    private HashSet<Pin> inputPins;
    private HashSet<Pin> outputPins;

    private DirectedAcyclicGraph<Pin, NetEdge> dag;
    private TopologicalOrderIterator<Pin, NetEdge> iterator, reverseIterator;

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
                        n.setLength(Math.abs(p1.pointX - p.pointX + p1.pointY - p.pointY));
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
                        dag.addDagEdge(p, p1, n);
                        //dag.addEdge(p, p1, n);
                    }
                    catch (DirectedAcyclicGraph.CycleFoundException ex) {
                        logger.info("Couldn't add edge {" + p.attachment + " " + p.pinName
                                + " : " + p1.attachment + " " + p1.pinName + "}");
                    }
                }
            }
        }
    }

    private void topologicalSort() {
        iterator = new TopologicalOrderIterator<>(dag);
        reverseIterator = new TopologicalOrderIterator<>(new EdgeReversedGraph<>(dag));
        /*
        while (iterator.hasNext()) {
            Pin p = iterator.next();
            System.out.print(p.attachment + " " + p.pinName + " ");
        }
        System.out.println();
        while (reverseIterator.hasNext()) {
            Pin p = reverseIterator.next();
            System.out.print(p.attachment + " " + p.pinName + " ");
        }
        System.out.println();
        */
    }

    void showLengths() {
        for (NetEdge edge :
                dag.edgeSet()) {
            System.out.println(edge.getName() + ": " + edge.getLength());
        }
    }

    void performSAT() {
        setPins();
        logger.trace("Initializing arrival time.");
        for (Pin p :
                dag.vertexSet()) {
            p.arrivalTime = dag.incomingEdgesOf(p).isEmpty() ? 0 + p.inDelay : - Double.MIN_VALUE;
            System.out.println(p + ": " + p.arrivalTime);
        }
        logger.info("Finished initializing AT.");
        logger.trace("Performing topological sort.");
        //topologicalSort();
        iterator = new TopologicalOrderIterator<>(dag);
        reverseIterator = new TopologicalOrderIterator<>(new EdgeReversedGraph<>(dag));
        logger.info("Finished topological sort.");
        logger.trace("Calculating AT.");
        while (iterator.hasNext()) {
            Pin p = iterator.next();
            for (NetEdge e :
                    dag.incomingEdgesOf(p)) {
                p.arrivalTime = Math.max(p.arrivalTime, dag.getEdgeSource(e).arrivalTime);
            }
            System.out.println(p + ": " + p.arrivalTime);
        }
        logger.info("Finished calculating AT");

        logger.trace("Initializing required time.");
        for (Pin p :
                dag.vertexSet()) {
            p.requiredTime = dag.outgoingEdgesOf(p).isEmpty() ? 0 : Double.MAX_VALUE;
            System.out.println(p + ": " + p.requiredTime);
        }
        logger.info("Finished initializing RAT.");
        logger.trace("Calculating RAT.");
        while (reverseIterator.hasNext()) {
            Pin p = reverseIterator.next();
            for (NetEdge e :
                    dag.outgoingEdgesOf(p)) {
                p.requiredTime = Math.min(p.requiredTime, dag.getEdgeTarget(e).requiredTime);
            }
            System.out.println(p + ": " + p.requiredTime);
        }
        logger.info("Finished calculating RAT.");

        logger.trace("Calculating slack.");
        for (Pin p :
                dag.vertexSet()) {
            p.slack = p.requiredTime - p.arrivalTime;
            System.out.println(p + ": " + p.slack);
        }
        logger.info("Finished calculating slack.");
    }

    void netWeighting() {
        logger.trace("Started net weighting.");
        for (NetEdge n :
                dag.edgeSet()) {
            n.setSlack(dag.getEdgeTarget(n).slack - dag.getEdgeSource(n).slack);
            n.setWeight(n.getSlack() < 0 ? 2 : 1);
            System.out.println(n + ": " + n.getWeight());
        }
        logger.info("Finished net weighting.");
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
