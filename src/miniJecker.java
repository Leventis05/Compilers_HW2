import syntaxtree.*;

public class miniJecker {
    public static void main(String[] args)
    {
        try {
            MiniJavaParser parser = new MiniJavaParser(System.in);

            Goal root = parser.Goal();

            SymbolTable st = new SymbolTable();

            symbolBuilder first = new symbolBuilder();

            root.accept(first, st);

            symbolJecker second = new symbolJecker();

            root.accept(second, st);

            System.out.println("Program jompiled");

        } catch (ParseException e) {
            System.err.println("Parse error: " + e.getMessage());
            return;

        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            return;
        }

        System.out.println("Program passed semantic analysis");
    }
}