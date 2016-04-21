import java.io.*;
import java.util.ArrayList;

/**
 * Created by HashmalS on 15.04.2016.
 * @author Sergey Soroka
 */
class DefReader {
    private String filename;
    private final String delims = "[ \t();+-]+";
    private ArrayList<Component> components = new ArrayList<>();

    DefReader(String name) {
        filename = name;
    }

    void readFile() throws IOException{
        File inputFile = new File(filename);
        FileInputStream fis = new FileInputStream(inputFile);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;

        long startTime = System.currentTimeMillis();

        while ((line = br.readLine()) != null) {
            if (line.length() > 0 && !(line.charAt(0) == '#')) {
                String[] splitStr = line.split(delims);
                if (line.startsWith("COMPONENTS")) {
                    for (int i = 0; i < Integer.parseInt(splitStr[1]); i++) {
                        line = br.readLine();
                        String[] splitStr1 = line.split(delims);
                        line = br.readLine();
                        String[] splitStr2 = line.split(delims);
                        components.add(new Component(splitStr1[1], splitStr1[2], splitStr2[1],
                                Integer.parseInt(splitStr2[2]), Integer.parseInt(splitStr2[3]),
                                splitStr2[4].charAt(0)));
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        double resultTime = ((double)endTime - (double)startTime) / 1000;
        System.out.println("\n\nTime consumed on reading from file: " + resultTime + " s.");

        System.out.println("Number of components created: " + components.size());

        br.close();
    }
}
