import syntaxtree.*;
import visitor.*;


public class symbolJecker extends GJDepthFirst<String, SymbolTable> {
    /** Jeck list:
     *      ~ Type checking
     *         - expressions
     *         - assignments
     *         - method calls
     *         - return types
     *      ~ Scope checking
     *         - var declaration
     *      ~ CFV
     *         - if
     *         - while
     *         - for (maybe?)
     *      ~ Inheritance
     * TODO Expression, Identifier, 
     */

    ClassInfo curClass;
    MethodInfo curMethod;
    
    /**
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass mc, SymbolTable st) {
        curClass = st.classes.get(mc.f1.toString()); // Fetch main class from st
        curMethod = curClass.methods.get(mc.f6.toString()); // Fetch main method from st

        mc.f15.accept(this, st);

        return null;
    }

    /** STATEMENTS
     * Grammar production:
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */

    /**
     * Grammar production:
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement sm, SymbolTable st) throws SemanticException {
        String typeLeft, typeRight;

        typeLeft = sm.f0.accept(this, st);
        typeRight = sm.f2.accept(this, st);

        if (!typeLeft.equals(typeRight))
            throw new SemanticException("No assignment op matches: " + typeLeft + ", " + typeRight);

        return null;
    }

    /**
     * Grammar production:
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement sm, SymbolTable st) throws SemanticException {
        String typeLeft = sm.f0.accept(this, st),
               indexType = sm.f2.accept(this, st),
               typeRight = sm.f5.accept(this, st);
        
        if (!indexType.equals("int"))
            throw new SemanticException("Array index must be int");
        
        if (!typeLeft.equals(typeRight))
            throw new SemanticException("No assignment op matches: " + typeLeft + ", " + typeRight);
 
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement sm, SymbolTable st) throws SemanticException {
        String cond = sm.f2.accept(this, st);

        if (!cond.equals("boolean"))
            throw new SemanticException("Branch condition must be boolean");
    
        return null;
    }

    public String visit(WhileStatement sm, SymbolTable st) throws SemanticException {
     
        return null;
    }

    public String visit(PrintStatement sm, SymbolTable st) throws SemanticException {
     
        return null;
    }



    /** Primary Expression
     * Grammar production:
     * f0 -> IntegerLiteral() !
     *       | TrueLiteral() !
     *       | FalseLiteral() !
     *       | Identifier() !
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression prex, SymbolTable st) {
        return prex.f0.accept(this, st);
    }

    //IntegerLiteral
    public String visit(IntegerLiteral exp, SymbolTable st) {
        return "int";
    }

    //TrueLiteral
    public String visit(TrueLiteral exp, SymbolTable st) {
        return "boolean";
    }

    //FalseLiteral
    public String visit(FalseLiteral exp, SymbolTable st) {
        return "boolean";
    }

    //Identifier
    public String visit(Identifier id, SymbolTable st) {
        //TODO check type in scope
        return "";
    }




    /** Expression
     * Grammar production:
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public String visit(AndExpression exp, SymbolTable st) {
        String type_A = exp.f0.accept(this, st), type_B = exp.f1.accept(this, st);

        if (type_A != "boolean" || type_B != "boolean") {
            throw new RuntimeException("error");
        }

        return "boolean";
    }

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */


}

