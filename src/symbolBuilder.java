import syntaxtree.*;
import visitor.*;

public class symbolBuilder extends GJDepthFirst<String, SymbolTable>{

    private ClassInfo curClass;
    private MethodInfo curMethod;

    //TODO overloading, dup methods or classes
    /**
     * Grammar production:
     * f1 -> Identifier()
     * f6 -> "main"
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     */
    public String visit(MainClass mc, SymbolTable st) {
        
        curClass = new ClassInfo();
        curClass.name = mc.f1.accept(this, st);
        curClass.parentName = null;

        curMethod = new MethodInfo();
        curMethod.name = mc.f6.toString();

        VarInfo argv = new VarInfo();
        argv.name = mc.f11.accept(this, st);
        argv.type = mc.f8.toString() + mc.f9.toString() + mc.f10.toString(); //String[]

        curMethod.parameters.put(argv.name, argv);
        
        if (st.classes.containsKey("MainClass"))
            throw new SemanticException("Main class appears twice");
        
        st.classes.put("MainClass", curClass);
        curClass.insertMethod(curMethod, st);
        
        mc.f14.accept(this, st);
        
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
        
        curClass = new ClassInfo();
        curClass.name = cd.f1.accept(this, st);
        curClass.parentName = null;

        if (st.classes.containsKey(curClass.name))
            throw new SemanticException("Class: " + curClass.name + " defined twice in this scope");
        
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
        
        ClassInfo parent;
        curClass = new ClassInfo();
        curClass.name = cd.f1.accept(this, st);
        curClass.parentName = cd.f3.accept(this, st);
        
        parent = st.getClass(curClass.parentName);
        if (parent == null)
            throw new SemanticException("Class: " + curClass.parentName + " not declared at this scope");
        if (parent.parentName != null)
            throw new SemanticException("Only single inheritance supported");

        curMethod = null;
        cd.f5.accept(this, st);
        cd.f6.accept(this, st);

        
        return null;
    }

    /**
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration vd, SymbolTable st) {
        
        VarInfo newVar = new VarInfo();

        newVar.type = vd.f0.accept(this, st);
        newVar.name = vd.f1.accept(this, st);

        if (curMethod != null) {
            //CHECK FOR DUPS IN METHOD
            if (curMethod.locals.containsKey(newVar.name))
                throw new SemanticException("Variable: " + newVar.name + " redeclared in this scope");
            curMethod.locals.put(newVar.name, newVar);

        } else {
            //CHECK FOR DUPS IN CLASS
            if (curClass.fields.containsKey(newVar.name))
                throw new SemanticException("Field: " + newVar.name + " declared twice in class " + curClass.name);
            curClass.fields.put(newVar.name, newVar);

        }

        
        return null;
    }

    /** METHOD
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
    
    curMethod = new MethodInfo();
    curMethod.returnType = md.f1.accept(this, st);
    curMethod.name = md.f2.accept(this, st);

    //call f4, f7
    md.f4.accept(this, st);
    md.f7.accept(this, st);

    curClass.insertMethod(curMethod, st);

    
    return null;
   }
   
    /**
    * Grammar production:
    * f0 -> Type() 
    * f1 -> Identifier()
    */
   public String visit(FormalParameter fp, SymbolTable st) {
    
    VarInfo nVar = new VarInfo();
    nVar.type = fp.f0.accept(this, st);
    nVar.name = fp.f1.accept(this, st);

    curMethod.parameters.put(nVar.name, nVar);
    
    return null;
   }


   /**
     * Grammar production:
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList fpl, SymbolTable st) {
        fpl.f0.accept(this, st);
        fpl.f1.accept(this, st);

        return null;
    }

   /**
     * Grammar production:
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail fpt, SymbolTable st) {
        fpt.f0.accept(this, st);

        return null;
    }
    
   /**
     * Grammar production:
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm fpl, SymbolTable st) {
        fpl.f1.accept(this, st);

        return null;
    }

    //Identifier
    public String visit(Identifier id, SymbolTable st) {
        return id.f0.toString();
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
}




