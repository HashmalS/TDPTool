import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HashmalS on 15.04.2016.
 * @author Sergey Soroka
 */
class DefReader {
    private String filename;
    private ArrayList<Component> components = new ArrayList<>();
    private ArrayList<Pin> pins = new ArrayList<>();
    private ArrayList<Net> nets = new ArrayList<>();
    private ArrayList<Row> rows = new ArrayList<>();
    private Design design;

    DefReader(String name) {
        filename = name;
    }

    void readFile() throws IOException {
        String[] splitStr1, splitStr2, splitStr3, splitStr4;
        final String delims = "[ \t();+-]+";

        File inputFile = new File(filename);
        FileInputStream fis = new FileInputStream(inputFile);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;

        long startTime = System.currentTimeMillis();

        while ((line = br.readLine()) != null) {
            if (line.length() > 0 && !(line.charAt(0) == '#')) {
                String[] splitStr = line.split(delims);

                if (line.startsWith("DESIGN")) {
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
                    do {
                        design.rows.add(new Row(splitStr[1], splitStr[2], Integer.parseInt(splitStr[3]),
                                Integer.parseInt(splitStr[4]), splitStr[5].charAt(0), Integer.parseInt(splitStr[7]),
                                Integer.parseInt(splitStr[9]), Integer.parseInt(splitStr[11]),
                                Integer.parseInt(splitStr[12])));
                        splitStr = br.readLine().split(delims);
                    } while (splitStr[0].equals("ROW"));
                }

                if (line.startsWith("COMPONENTS")) {
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        splitStr1 = br.readLine().split(delims);
                        splitStr2 = br.readLine().split(delims);
                        design.components.add(new Component(splitStr1[1], splitStr1[2], splitStr2[1],
                                Integer.parseInt(splitStr2[2]), Integer.parseInt(splitStr2[3]),
                                splitStr2[4].charAt(0)));
                    }
                }

               if (line.startsWith("PINS")) {
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        splitStr1 = br.readLine().split(delims);
                        splitStr2 = br.readLine().split(delims);
                        splitStr3 = br.readLine().split(delims);
                        splitStr4 = br.readLine().split(delims);
                        design.pins.add(new Pin(splitStr1[1], splitStr1[3], splitStr2[2], splitStr3[1],
                                Integer.parseInt(splitStr3[2]), Integer.parseInt(splitStr3[3]),
                                splitStr3[4].charAt(0), splitStr4[2],
                                Integer.parseInt(splitStr4[3]), Integer.parseInt(splitStr4[4]),
                                Integer.parseInt(splitStr4[5]), Integer.parseInt(splitStr4[6])));
                    }
                }

                if(line.startsWith("NETS")) {
                    Map<String, String> connectionsMap = new HashMap<>();
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        splitStr1 = br.readLine().split(delims);
                        for (int j = 2; j < splitStr1.length - 1; j++) {
                            connectionsMap.put(splitStr1[j++], splitStr1[j]);
                        }
                        design.nets.add(new Net(splitStr1[0], connectionsMap));
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        double resultTime = ((double)endTime - (double)startTime) / 1000;
        System.out.println("\n\nTime consumed on reading from file: " + resultTime + " s.");


        System.out.println("Found design \"" + design.getDesignName() + "\"\n\nDesign info:");
        design.showDesignInfo();

        br.close();
    }
}
