import java.io.IOException;

/**
 * Created by HashmalS on 15.04.2016.
 * @author Sergey Soroka
 */
public class Program {

    public static void main(String[] args) {
        DefReader reader = new DefReader("src\\main\\resources\\TestFiles\\simple.def");
        try {
            reader.readFile();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
