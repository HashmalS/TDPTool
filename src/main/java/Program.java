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

    public static void main(String[] args) {
        logger.trace("Entering application.");

        FileReader reader = new FileReader("src\\main\\resources\\TestFiles\\b19\\b19.def",
                "src\\main\\resources\\TestFiles\\b19\\techlib.lef");
        try {
            reader.readFile();
            reader.readComponentPins();
        } catch (IOException ex) {
            logger.error(ex);
        }

        logger.trace("Exiting application.");
    }
}
