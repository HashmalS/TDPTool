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

        FileReader reader = new FileReader("C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\mgc_edit_dist\\mgc_edit_dist.def",
                "C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\mgc_edit_dist\\techlib.lef");
        try {
            design = reader.readDesign();
        } catch (IOException ex) {
            logger.error(ex);
        }


        logger.info("Started calculating wire length.");

        long startTime = System.currentTimeMillis();
        design.calculateWireLength();
        long endTime = System.currentTimeMillis();
        double resultTime = ((double)endTime - (double)startTime) / 1000;

        logger.info("Finished calculating wire length.");
        logger.info("Time consumed on calculating wire length: " + resultTime + " s.");
        design.calculateWireLength();

        logger.trace("Started writing a file.");
        FileWriter writer = new FileWriter("C:\\Users\\HashmalS\\Documents\\Diploma\\benchmarks\\mgc_edit_dist");
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
