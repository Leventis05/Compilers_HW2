import syntaxtree.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class miniJecker {
    public static void main(String[] args)
    {
        int error_count = 0;

        if (args.length < 1) {
            try {
                parseAnCheck(System.in);
                
            } catch (ParseException e) {
                System.err.println("Parse error: " + e.getMessage());
                error_count++;
                return;

            } catch (Exception e) {
                System.err.println("Compilation error: " + e.getMessage());
                error_count++;
                return;
            }

        }


        for (String arg : args) {
            try {
                FileInputStream file = new FileInputStream(arg);
                parseAnCheck(file);

            } catch (ParseException e) {
                System.err.println("Parse error: " + e);
                error_count++;
                continue;

            } catch (Exception e) {
                System.err.println("Compilation error: " + e);
                error_count++;
                continue;
            }
        }
        

        if (error_count == 0)
            System.out.println("Program jompiled");
    }


    private static void parseAnCheck(InputStream is) throws ParseException {
        MiniJavaParser parser = new MiniJavaParser(is);

        Goal root = parser.Goal();

        SymbolTable st = new SymbolTable();

        symbolBuilder first = new symbolBuilder();

        
        root.accept(first, st);
        
        System.out.println("Cooked");

        symbolJecker second = new symbolJecker();

        root.accept(second, st);
    }
}