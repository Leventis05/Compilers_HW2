import syntaxtree.*;
import visitor.*;

public class miniJecker {
    public static void main(String[] args)
    {
        try {
            MiniJavaParser parser = new MiniJavaParser(System.in);

            Goal root = parser.Goal();

            System.out.println("Program jompiled");

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}