import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by HashmalS on 15.04.2016.
 * @author Sergey Soroka
 */
public class Program {
    // Configuring Log4j
    private static final Logger logger = LogManager.getLogger(Program.class);

    private static Design design;

    public static void main(String[] args) {
        logger.trace("Entering application.");

        FileReader reader = new FileReader("C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\b19\\b19.def",
                "C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\b19\\techlib.lef",
                "C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\b19\\b19.sdc");
        try {
            design = reader.readDesign();
        } catch (IOException ex) {
            logger.error(ex);
        }

        logger.trace("Started calculating wire length.");
        long startTime = System.currentTimeMillis();
        design.calculateWireLength();
        long endTime = System.currentTimeMillis();
        double resultTime = ((double)endTime - (double)startTime) / 1000;
        logger.trace("Finished calculating wire length.");
        logger.info("Time consumed on calculating wire length: " + resultTime + " s.");

        design.componentsToRows();

        logger.trace("Creating graph.");
        design.createAcyclicGraph();
        logger.trace("Successfully created graph.");
        //design.checkPaths();
        //design.showLengths();
        //design.performSAT();
        //design.netWeighting();
        design.statisticalSTA();
        design.showTNS();
        design.place();
        design.showTNS();

        logger.trace("Started writing a file.");
        FileWriter writer = new FileWriter("C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\b19");
        try {
            writer.writeDesign(design);
        }
        catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        logger.trace("Finished writing.");

        logger.trace("Exiting application.");
    }
}
