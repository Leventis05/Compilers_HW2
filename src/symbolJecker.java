import syntaxtree.*;
import visitor.*;


public class symbolJecker extends GJDepthFirst<String, SymbolTable> {
    /** Jeck list:
     * TODO make bracket expr
     */

    private ClassInfo curClass;
    private MethodInfo curMethod;
    
    /**
     * Grammar production:
     * f1 -> Identifier()
     * f6 -> "main"
     * f15 -> ( Statement() )*
     */
    public String visit(MainClass mc, SymbolTable st) {
        curClass = st.getClass("MainClass"); // Fetch main class from st
        String[] args = {"String[]"};
        curMethod = curClass.getMethod(mc.f6.toString(), args, st); // Fetch main method from st

        mc.f15.accept(this, st);
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration cd, SymbolTable st) {
        
        curClass = st.getClass(cd.f1.accept(this, st));
        curMethod = null;
        cd.f3.accept(this, st);
        cd.f4.accept(this, st);
        
        st.classes.put(curClass.name, curClass);
    
        
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration cd, SymbolTable st) {
        
        curClass = st.getClass(cd.f1.accept(this, st));
        curMethod = null;
        cd.f3.accept(this, st);
        cd.f4.accept(this, st);
        
        st.classes.put(curClass.name, curClass);
        
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration md, SymbolTable st) {
        String meth = md.f2.accept(this, st);
        String params_s = md.f4.accept(this, st);
        String[] params = (params_s != null) ? params_s.split(",") : null;
        curMethod = curClass.getMethod(meth, params, st);
        md.f8.accept(this, st);
        md.f10.accept(this, st);
        return null;
    }

    /** list
     * Grammar production:
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList fp, SymbolTable st) {
        String first = fp.f0.accept(this, st);
        String tail = fp.f1.accept(this, st);
        return first + "," + tail;
    }
    
    /**fp
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter fp, SymbolTable st) {
        return fp.f0.accept(this, st);
    }

    /**tail
     * Grammar production:
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail fp, SymbolTable st) {
        String ret = "";

        for (Node node : fp.f0.nodes) {
            ret += fp.f0.accept(this, st);
        }
        return ret;
    }


    /** term
     * Grammar production:
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm fp, SymbolTable st) {
        return fp.f1.accept(this, st);
    }

    /**
     * Grammar production:
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, SymbolTable st) {
        return n.f0.accept(this, st);
    }

    public String visit(ArrayType n, SymbolTable st) {
        return n.f0.toString();
    }

    public String visit(BooleanType n, SymbolTable st) {
        return n.f0.toString();
    }

    public String visit(IntegerType n, SymbolTable st) {
        return n.f0.toString();
    }

    /** STATEMENTS
     * Grammar production:
     * f0 -> Block() !
     *       | AssignmentStatement() !
     *       | ArrayAssignmentStatement() !
     *       | IfStatement() !
     *       | WhileStatement() !
     *       | PrintStatement() !
     */

    public String visit(Statement s, SymbolTable st) {
        s.f0.accept(this, st);
        return null;
    }

    /**
     * Grammar production:
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement sm, SymbolTable st) {
        String nameLeft, typeRight;
        VarInfo leftVar;
        System.out.println("he");
        nameLeft = sm.f0.accept(this, st);
        typeRight = sm.f2.accept(this, st);
        leftVar = st.checkGetVar(curClass, curMethod, nameLeft);

        if (leftVar == null)
            throw new SemanticException("Identifier: " + nameLeft + " could not be resolved to a variable");

        if (!st.objEq(leftVar.type, typeRight))
            throw new SemanticException("No assignment op matches: " + leftVar.type + ", " + typeRight);         


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
        String nameLeft = sm.f0.accept(this, st),
               indexType = sm.f2.accept(this, st),
               typeRight = sm.f5.accept(this, st);
        VarInfo varL = st.checkGetVar(curClass, curMethod, nameLeft);
        
        if (varL == null)
            throw new SemanticException("Identifier: " + nameLeft + " could not be resolved to a variable");

        if (!indexType.equals("int"))
            throw new SemanticException("Array index must be int");
        
        if (!st.objEq(varL.type, typeRight))
            throw new SemanticException("No assignment op matches: " + varL.type + ", " + typeRight);
 
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

        System.out.println("i will beat u");
        sm.f4.accept(this, st);
        return null;
    }

    /** Print Statement
     * Grammar production:
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement sm, SymbolTable st) {
        sm.f2.accept(this, st);
        return null;
    }

    /** Expressions
     * Grammar production:
     * f0 -> AndExpression() !
     *       | CompareExpression() !
     *       | PlusExpression() !
     *       | MinusExpression() !
     *       | TimesExpression() !
     *       | ArrayLookup() !
     *       | ArrayLength() !
     *       | MessageSend() !
     *       | PrimaryExpression()
     */
    public String visit(AndExpression exp, SymbolTable st) {
        String type_A = exp.f0.accept(this, st), type_B = exp.f2.accept(this, st);

        checkLRtypes(type_A, type_B, "boolean", "AND op expects bool", st);
        return "boolean";
    }

    /** <
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression ce, SymbolTable st) {
        String type_A = ce.f0.accept(this, st), type_B = ce.f2.accept(this, st);

        checkLRtypes(type_A, type_B, "int", "Compare op expects int", st);
        return "boolean";
    }

    //+
    public String visit(PlusExpression pe, SymbolTable st) {
        String type_A = pe.f0.accept(this, st), type_B = pe.f2.accept(this, st);

        checkLRtypes(type_A, type_B, "int", "PLUS op expects int", st);
        return "int";
    }

    //-
    public String visit(MinusExpression e, SymbolTable st) {
        String type_A = e.f0.accept(this, st), type_B = e.f2.accept(this, st);
        checkLRtypes(type_A, type_B, "int", "MINUS op expects int", st);
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

        checkLRtypes(inType, inType, "int", "Array lookup expects int", st);
        
        //CHECK FOR ARGV
        if (arrType.equals("String[]"))
            if (!curClass.name.equals(st.getClass("MainClass").name) || 
                !curMethod.name.equals("main"))
                throw new SemanticException("argv called on non main function");
            else
                return "String";

        checkLRtypes(arrType, arrType, "int[]", "Lookup op used on non array obj", st);
        return "int";
    }

    /** Arr len
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength e, SymbolTable st) {
        String arrType = e.f0.accept(this, st);

        if (!arrType.equals("int[]") && !arrType.equals("String[]"))
            throw new SemanticException("Length Expression used on non array object");

        return "int";
    }

    /**Method Call
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend e, SymbolTable st) {
        // Get calling obj and callee method
        String objType = e.f0.accept(this, st), methName = e.f2.accept(this, st);
        // Get and parse arguments given 
        String args = e.f4.accept(this, st);

        String[] argsList = (args != null) ? args.split(",") : null;

        // Get classinfo of obj and parent if there is one
        ClassInfo obj = st.getClass(objType),
        parent = (obj != null && obj.parentName != null) ? st.getClass(obj.parentName) : null;
        // Get methodinfo of callee if not found in class check also in parent
        MethodInfo callee = (obj != null) ? obj.getMethod(methName, argsList, st) : null;
        if (callee == null && parent != null)
            callee = parent.getMethod(methName, argsList, st);
        
        // If class or method is undefined throw error
        if (obj == null)
            throw new SemanticException("Class: " + objType + " is not declared in this scope");
        
        if (callee == null)
            throw new SemanticException("Class: " + objType + " does not contain any method: " + methName);
        return callee.returnType;
    }


    /** Primary Expressions
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
        return id.f0.toString();
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
        String name = ae.f1.accept(this, st);
        ClassInfo obj = st.getClass(name);

        if (obj == null || name == "MainClass")
            throw new SemanticException("Object: " + name + " couldn't be allocated");

        return obj.name;
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


    // HANDLE EXP LISTS  
    /**Exp list
     * Grammar production:
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList e, SymbolTable st) {
        String argType = e.f0.accept(this, st), argTail = e.f1.accept(this, st);
        VarInfo var;
    
        // check type
        if (argType != null) {
            var = st.checkGetVar(curClass, curMethod, argType);
            if (var != null)
                argType = var.type;
        }
    
        return argType + argTail;
    }

    /**Exp tail
     * Grammar production:
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail e, SymbolTable st) {
        String types = "", tmp;
        VarInfo var;

        for (Node node : e.f0.nodes) {
            System.out.println("sugma");
            tmp = "," + node.accept(this, st);
            // Check type
            var = st.checkGetVar(curClass, curMethod, tmp);
            System.out.println("weneira: " + tmp);
            if (var != null)
                tmp = var.type;
            types += tmp;
        }


        return types;
    }

    public String visit(ExpressionTerm e, SymbolTable st) {
        return e.f1.accept(this, st);
    }

    public String visit(Expression e, SymbolTable st) {
        return e.f0.accept(this, st);
    }


    /* HELPERS */
    public String checkLRtypes(String type_A, String type_B, String valid_t, String msg, SymbolTable st) {
        String t1 = type_A, t2 = type_B;

        VarInfo var = st.checkGetVar(curClass, curMethod, type_A);
        if (var != null)
            t1 = var.type;

        var = st.checkGetVar(curClass, curMethod, type_B);
        if (var != null)
            t2 = var.type;

        if (!t1.equals(valid_t) || !t2.equals(valid_t))
            throw new SemanticException(msg);

        else
            return null;
    }

}

