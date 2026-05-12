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
     * TODO check type decl
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
    public String visit(AssignmentStatement sm, SymbolTable st) {
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
    public String visit(ArrayAssignmentStatement sm, SymbolTable st) {
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
    public String visit(IfStatement sm, SymbolTable st) {
        String cond = sm.f2.accept(this, st);

        if (!cond.equals("boolean"))
            throw new SemanticException("Branch condition must be boolean");
    
        sm.f4.accept(this, st);
        sm.f6.accept(this, st);
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement sm, SymbolTable st) {
        String cond = sm.f2.accept(this, st);

        if (!cond.equals("boolean"))
            throw new SemanticException("while statement expects boolean");

        sm.f4.accept(this, st);

        return null;
    }


    /** Expression
     * Grammar production:
     * f0 -> AndExpression() !
     *       | CompareExpression() !
     *       | PlusExpression() !
     *       | MinusExpression() !
     *       | TimesExpression() !
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */

    /** &&
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public String visit(AndExpression exp, SymbolTable st) {
        String type_A = exp.f0.accept(this, st), type_B = exp.f1.accept(this, st);

        checkLRtypes(type_A, type_B, "boolean", "AND op expects bool");
        return "boolean";
    }

    /** <
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression ce, SymbolTable st) {
        String type_A = ce.f0.accept(this, st), type_B = ce.f1.accept(this, st);

        checkLRtypes(type_A, type_B, "int", "Compare op expects int");
        return "boolean";
    }

    //+
    public String visit(PlusExpression pe, SymbolTable st) {
        String type_A = pe.f0.accept(this, st), type_B = pe.f1.accept(this, st);

        checkLRtypes(type_A, type_B, "int", "PLUS op expects int");
        return "int";
    }

    //-
    public String visit(MinusExpression e, SymbolTable st) {
        String type_A = e.f0.accept(this, st), type_B = e.f1.accept(this, st);

        checkLRtypes(type_A, type_B, "int", "MINUS op expects int");
        return "int";
    }

    //Times
    public String visit(TimesExpression e, SymbolTable st) {
        String type_A = e.f0.accept(this, st), type_B = e.f1.accept(this, st);

        checkLRtypes(type_A, type_B, "int", "TIMES op expects int");
        return "int";
    }

    /** Arr lookup
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup e, SymbolTable st) {
        String arrType = e.f0.accept(this, st), inType = e.f2.accept(this, st);

        if (arrType.equals("String[]")) 
            if ()
            
        checkLRtypes(arrType, arrType, "int[]", "Lookup op used on non array obj");
        return "int";
    }

    /** Primary Expression
     * Grammar production:
     * f0 -> IntegerLiteral() !
     *       | TrueLiteral() !
     *       | FalseLiteral() !
     *       | Identifier() !
     *       | ThisExpression() !
     *       | ArrayAllocationExpression() !
     *       | AllocationExpression() !
     *       | NotExpression() !
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
        String name = id.f0.toString();
        String type = st.checkGetVarType(curClass, curMethod, name);

        if (type != null)
            return type;
        else
            throw new SemanticException("Identifier: " + name + " has not been declared in this scope");
    }

    //ThisExpression
    public String visit(ThisExpression te, SymbolTable st) {
        return curClass.name;
    }

    /** ArrayAllocationExpression
     * Grammar production:
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression aae, SymbolTable st) {
        String sizeType = aae.f3.accept(this, st);

        if (!sizeType.equals("int"))
            throw new SemanticException("Array allocation expects integer");
        
        return "int[]";
    }

    /** Alloc Expression
     * Grammar production:
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression ae, SymbolTable st) {
        return ae.f1.accept(this, st);
    }

    /**
     * Grammar production:
     * f0 -> "!"
     * f1 -> PrimaryExpression()
     */
    public String visit(NotExpression ne, SymbolTable st) {
        String type = ne.f1.accept(this, st);
        
        if (!type.equals("boolean"))
            throw new SemanticException("not expression ! expects boolean");

        return "boolean";
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

    

    public String checkLRtypes(String type_A, String type_B, String valid_t, String msg) {

        if (!type_A.equals(valid_t) || !type_B.equals(valid_t))
            throw new SemanticException(msg);

        else
            return null;
    }

}

