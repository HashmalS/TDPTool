import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created on 09.05.2016.
 * @author Sergey Soroka
 */
class FileWriter {
    private String fileName;

    FileWriter(String path) {
        fileName = "output.def";
        fileName = path + "\\" + fileName;
    }

    void writeDesign(Design design) throws IOException {
        Path path = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path)){
            String header = "VERSION 5.7 ; \n" + "DIVIDERCHAR \"/\" ;\n";
            writer.write(header);
            writer.write(design.toString());
            for (Row row :
                    design.rows) {
                writer.write(row.toString());
            }

            writer.write("\nCOMPONENTS " + design.components.size() + " ;\n");
            for (Component comp :
                    design.components) {
                writer.write(comp.fileWritingString());
                writer.flush();
            }
            writer.write("END COMPONENTS\n\n");

            writer.write("PINS " + design.globalPins.size() + " ; \n");
            for (GlobalPin pin :
                    design.globalPins) {
                writer.write(pin.fileWritingString());
                writer.flush();
            }
            writer.write("END PINS\n\n");

            writer.write("NETS " + design.nets.size() + " ; \n");
            for (Net net :
                    design.nets) {
                writer.write(net.fileWritingString());
                writer.flush();
            }
            writer.write("END NETS \n\n");

            writer.write("END DESIGN\n");
        }
    }
}
