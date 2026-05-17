import java.util.HashMap;

public class SymbolTable {

    // GLOBAL scope: class name → ClassInfo
    public HashMap<String, ClassInfo> classes = new HashMap<>();

    public void printDebug() {
        for (String key : this.classes.keySet())
            System.out.println("yunara: " + key + " waneira: " + this.classes.get(key).name);
    }

    public void printFull() {
        System.out.println("Symbols:");
        for (String key : this.classes.keySet()) {
            System.out.println("Class:");
            this.classes.get(key).print();
        }
        
    }

    public ClassInfo getClass(String name) {
        for (String key : classes.keySet())
            if (key.equals(name))
                return classes.get(key);
            else if (key.equals("MainClass"))
                if (classes.get(key).name.equals(name))
                    return classes.get(key);
        
            return null;
    }

    public VarInfo checkGetVar(ClassInfo cClass, MethodInfo method, String name) {

        VarInfo ret = method.getVar(name);
        if (ret == null)
            ret = cClass.getField(name, this);
        
        return ret;
    }

    public boolean objEq(String type_A, String type_B) {
        if (!type_A.equals(type_B)) {
            if (this.classes.containsKey(type_A) && this.classes.containsKey(type_B)) {
                ClassInfo cL = this.classes.get(type_A);
                ClassInfo cR = this.classes.get(type_B);
                
                if (!cL.name.equals(cR.parentName))
                    return false;
                
            } else
                return false;
        }

        return true;
    }
}

/* SYMBOL TABLE CLASSES */
class ClassInfo {
    String name;
    String parentName;

    HashMap<String, VarInfo> fields = new HashMap<>();
    /** Key here is a cat: "method_name|overload_index" */ 
    HashMap<String, MethodInfo> methods = new HashMap<>();

    public void print() {
        System.out.println("    Name: " + name);
        System.out.println("    Parent Name: " + ((parentName != null) ? parentName : "none"));
        System.out.println("    Fields: ");
        for (String key : fields.keySet()) {
            System.out.println("        " + fields.get(key).type + ": " + fields.get(key).name);
        }

        System.out.println("    Methods: ");
        for (String key : methods.keySet()) {
            methods.get(key).print();
        }
    }

    public void insertMethod(MethodInfo nMethod, SymbolTable st) {
        String tmp;
        String[] params = nMethod.getParamTypes(), overChecker;
        int index = 0, i;
        MethodInfo overld;

        while (true) {
            tmp = nMethod.name + index;
            overld = methods.get(tmp);
            if (overld == null)
                break;

            // Check diff in params
            overChecker = overld.getParamTypes();

            if (params.length != overChecker.length) {
                for (i = 0; i < params.length; i++)
                    if (!st.objEq(overChecker[i], params[i]))
                        break;
                
                if (i == params.length)
                    // Method matches
                    throw new SemanticException("Overloading requires at least one arg of different type");
            }

            index++;
        }

        nMethod.overloadIndex = index;
        methods.put(tmp, nMethod);
    }

    public VarInfo getField(String name, SymbolTable st) {
        VarInfo var = null;
        ClassInfo parent = (parentName != null) ? st.classes.get(parentName) : null;

        // Try current class
        var = fields.get(name);
        
        if (var != null)
            return var;

        // Not found and no parent to search
        if (parent == null)
            return null;

        // We can do this because ther is only single inheritance
        return parent.getField(name, st);
    }

    public MethodInfo getMethod(String name, String[] args, SymbolTable st) {
        MethodInfo ret = null;
        String[] params;
        int i;

        for (String key : methods.keySet()) {
            // Init
            ret = null;

            // Check if there is a chance that this is the right method
            if (key.contains(name)) {
                ret = methods.get(key);
                
                // Confirm through methodinfo.name
                if (ret.name.equals(name)) {
                    // Get method params
                    params = ret.getParamTypes();
                    
                    // Check len
                    if (params.length != args.length) {
                        ret = null;
                        continue;
                    }
                    
                    // Check types one by one
                    for (i = 0; i < args.length; i++)
                        if (!st.objEq(args[i], params[i]))
                            break;
                        
                        
                    if (i != args.length)
                        // Method didnt match
                        continue;
                    else
                        // Method matches
                        break;
                }
            }
        }


        return ret;
    }
}

class MethodInfo {
    String name;
    String returnType;
    /** Index to differentiate overloaded functions.
    *   First function has overloadIndex = 0 second 1 etc */
    int overloadIndex;

    // Name , info
    HashMap<String, VarInfo> parameters = new HashMap<>();
    HashMap<String, VarInfo> locals = new HashMap<>();

    public void print() {
        System.out.println("        Name: " + name);
        System.out.println("            Ret Type: " + ((returnType != null) ? returnType : "void"));
        System.out.println("            Locals:");
        for (String key : locals.keySet())
            System.out.println("                " + locals.get(key).type + ": " + locals.get(key).name);

        System.out.println("            Parameters:");
        for (String key : parameters.keySet())
            System.out.println("                " + parameters.get(key).type + ": " + parameters.get(key).name);

    }

    /** Get var first from param and then locals */
    public VarInfo getVar(String name) {
        VarInfo ret = null;
        ret = parameters.get(name);
        
        if (ret == null)
            ret = locals.get(name);
            
        return ret;
    }

    public String[] getParamTypes() {
        String ret = "";
        for (String param : parameters.keySet())
            ret += parameters.get(param).type + ",";

        return ret.split(",");
    }
}

class VarInfo {
    String name;
    String type;
}