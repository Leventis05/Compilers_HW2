import syntaxtree.*;
import visitor.*;

public class symbolBuilder extends GJDepthFirst<String, SymbolTable>{

    ClassInfo curClass;
    MethodInfo curMethod;

    //TODO check duplicates


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
        curClass.name = mc.f1.toString();
        curClass.parentName = null;

        curMethod = new MethodInfo();
        curMethod.name = mc.f6.toString();

        VarInfo argv = new VarInfo();
        argv.name = mc.f11.toString();
        argv.type = mc.f8.toString() + mc.f9.toString() + mc.f10.toString(); //String[]

        curMethod.parameters.put(argv.name, argv);
        
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
        curClass.name = cd.f1.toString();
        curClass.parentName = null;
        
        cd.f3.accept(this, st);
        cd.f4.accept(this, st);

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

        curClass = new ClassInfo();
        curClass.name = cd.f1.toString();
        curClass.parentName = cd.f3.toString();
        
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

        newVar.type = vd.f0.toString();
        newVar.name = vd.f1.toString();

        if (curMethod != null) {
            curMethod.locals.put(newVar.name, newVar);
        } else {
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
    curMethod.returnType = md.f1.toString();
    curMethod.name = md.f2.toString();

    //call f4, f7
    md.f4.accept(this, st);
    md.f7.accept(this, st);

    curClass.methods.put(curMethod.name, curMethod);

    return null;
   }
   
    /**
    * Grammar production:
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter fp, SymbolTable st) {
    VarInfo nVar = new VarInfo();
    nVar.type = fp.f0.toString();
    nVar.name = fp.f1.toString();

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
}




