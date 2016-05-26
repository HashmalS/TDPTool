import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by HashmalS on 15.04.2016.
 * @author Sergey Soroka
 */
class FileReader {
    private String defName;
    private String lefName;
    private Design design;
    private Pin connectedPin = null;

    private final String delims = "[\t( );+-]+";
    private long startTime, endTime;
    private Comparator<Component> componentComparator = (o1, o2) ->  o1.compName.compareTo(o2.compName);

    private static final Logger logger = LogManager.getLogger(Program.class.getName());

    FileReader(String defFile, String lefFile) {
        defName = defFile;
        lefName = lefFile;
    }

    private void readComponentPins() throws IOException {
        File lefFile = new File(lefName);
        FileInputStream lefInputStream = new FileInputStream(lefFile);

        BufferedReader lefBufferedReader = new BufferedReader(new InputStreamReader(lefInputStream));

        logger.info("Started parsing LEF file");

        String line, model;
        String[] splitStr;

        startTime = System.currentTimeMillis();
        while ((line = lefBufferedReader.readLine()) != null) {
            if (line.length() > 0 && !(line.charAt(0) == '#')) {
                if(line.startsWith("MACRO")) {
                    splitStr = line.split(delims);
                    model = splitStr[1];
                    while (!(splitStr[0].equals("END") && splitStr[1].equals(model))) {
                        splitStr = lefBufferedReader.readLine().split(delims);
                        if (splitStr.length > 1 && splitStr[1].equals("PIN")) {
                            for (Component comp: design.components) {
                                if (comp.modelName.equals(model)) {
                                    comp.pins.add(new ComponentPin(splitStr[2], splitStr[4], comp));
                                }
                            }
                        }
                        if (splitStr.length > 1 && splitStr[1].equals("SIZE")) {
                            for (Component comp: design.components) {
                                if (comp.modelName.equals(model)) {
                                    comp.width = Double.parseDouble(splitStr[2]);
                                }
                            }
                        }
                    }
                }
            }
        }

        endTime = System.currentTimeMillis();
        double resultTime = ((double)endTime - (double)startTime) / 1000;
        logger.info("Time consumed on reading from LEF file: " + resultTime + " s.");

        logger.info("Finished parsing LEF file.");
    }

    Design readDesign() throws IOException {
        String[] splitStr1, splitStr2, splitStr3, splitStr4;

        File defFile = new File(defName);
        FileInputStream defInputStream = new FileInputStream(defFile);

        BufferedReader defBufferedReader = new BufferedReader(new InputStreamReader(defInputStream));

        logger.info("Started parsing DEF file.");

        String line;

        startTime = System.currentTimeMillis();

        while ((line = defBufferedReader.readLine()) != null) {
            if (line.length() > 0 && !(line.charAt(0) == '#')) {
                String[] splitStr = line.split(delims);

                if (line.startsWith("DESIGN")) {
                    logger.info("Found design.");
                    design = new Design(splitStr[1]);
                }

                if (line.startsWith("UNITS")) {
                    design.setDbuPerMicron(Integer.parseInt(splitStr[3]));
                }

                if (line.startsWith("DIEAREA")) {
                    int[] point1 = {Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2])};
                    int[] point2 = {Integer.parseInt(splitStr[3]), Integer.parseInt(splitStr[4])};
                    design.setDieArea1(point1);
                    design.setDieArea2(point2);
                    design.calculateSize();
                }

                if (line.startsWith("ROW")) {
                    logger.info("Parsing rows.");
                    do {
                        design.rows.add(new Row(splitStr[1], splitStr[2], Integer.parseInt(splitStr[3]),
                                Integer.parseInt(splitStr[4]), splitStr[5].charAt(0), Integer.parseInt(splitStr[7]),
                                Integer.parseInt(splitStr[9]), Integer.parseInt(splitStr[11]),
                                Integer.parseInt(splitStr[12])));
                        splitStr = defBufferedReader.readLine().split(delims);
                    } while (splitStr[0].equals("ROW"));
                    logger.info("Success.");
                }

                if (line.startsWith("COMPONENTS")) {
                    logger.info("Parsing components.");
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        splitStr1 = defBufferedReader.readLine().split(delims);
                        splitStr2 = defBufferedReader.readLine().split(delims);
                        design.components.add(new Component(splitStr1[1], splitStr1[2], splitStr2[1],
                                Integer.parseInt(splitStr2[2]), Integer.parseInt(splitStr2[3]),
                                splitStr2[4].charAt(0)));
                    }
                    Collections.sort(design.components, componentComparator);
                    logger.info("Success.");
                    readComponentPins();
                }

                    if (line.startsWith("PINS")) {
                    logger.info("Parsing global pins.");
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        splitStr1 = defBufferedReader.readLine().split(delims);
                        splitStr2 = defBufferedReader.readLine().split(delims);
                        splitStr3 = defBufferedReader.readLine().split(delims);
                        splitStr4 = defBufferedReader.readLine().split(delims);
                        design.globalPins.add(new GlobalPin(splitStr1[1], splitStr1[3], splitStr2[2], splitStr3[1],
                               Integer.parseInt(splitStr3[2]), Integer.parseInt(splitStr3[3]),
                               splitStr3[4].charAt(0), splitStr4[2],
                               Integer.parseInt(splitStr4[3]), Integer.parseInt(splitStr4[4]),
                               Integer.parseInt(splitStr4[5]), Integer.parseInt(splitStr4[6])));
                    }
                    logger.info("Success.");
                }

                if(line.startsWith("NETS")) {
                    logger.info("Parsing nets.");
                    ArrayList<Pin> connections = new ArrayList<>();
                    connections.clear();
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        splitStr1 = defBufferedReader.readLine().split(delims);
                        connections = new ArrayList<>();
                        for (int j = 2; j < splitStr1.length - 1; j+=2) {
                            if (splitStr1[j].equals("PIN")) {
                                for (GlobalPin pin:
                                        design.globalPins) {
                                    if (pin.pinName.equals(splitStr1[j+1])) {
                                        connectedPin = pin;
                                    }
                                }
                            }
                            else {
                                int index = Collections.binarySearch(design.components, new Component(splitStr1[j]),
                                        componentComparator);
                                for (ComponentPin pin:
                                        design.components.get(index).pins) {
                                    if (pin.pinName.equals(splitStr1[j+1])) {
                                        connectedPin = pin;
                                    }
                                }
                            }
                            connections.add(connectedPin);
                        }
                        design.nets.add(new Net(splitStr1[1], connections));
                    }
                    logger.info("Success.");
                }
            }
        }

        endTime = System.currentTimeMillis();
        double resultTime = ((double) endTime - (double) startTime) / 1000;
        logger.info("Finished reading from DEF file.");
        logger.info("Time consumed on reading from files: " + resultTime + " s.");

        System.out.println("Found design \"" + design.getDesignName() + "\"\n\nDesign info:");
        design.showDesignInfo();
        defBufferedReader.close();
        return design;
    }
}
