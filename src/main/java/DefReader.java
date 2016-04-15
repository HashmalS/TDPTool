import java.io.*;

/**
 * Created by HashmalS on 15.04.2016.
 * @author Sergey Soroka
 */
class DefReader {
    private String filename;

    DefReader(String name) {
        filename = name;
    }

    void readFile() throws IOException{
        File inputFile = new File(filename);
        FileInputStream fis = new FileInputStream(inputFile);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        br.close();
    }
}
