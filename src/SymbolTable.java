import java.util.HashMap;

public class SymbolTable {

    // GLOBAL scope: class name → ClassInfo
    public HashMap<String, ClassInfo> classes = new HashMap<>();

    public String checkGetVarType(ClassInfo cClass, MethodInfo method, String name) {
        ClassInfo parent = classes.get(cClass.parentName);
        
        if (method.parameters.containsKey(name))
            return method.parameters.get(name).type;
        
        else if (method.locals.containsKey(name))
            return method.locals.get(name).type;

        else if (cClass.fields.containsKey(name))
            return cClass.fields.get(name).type;

        else if (parent.fields.containsKey(name))
            return parent.fields.get(name).type;
        
        else
            return null;
    }

}

/* SYMBOL TABLE CLASSES */
class ClassInfo {
    String name;
    String parentName;

    HashMap<String, VarInfo> fields;
    HashMap<String, MethodInfo> methods;
}

class MethodInfo {
    String name;
    String returnType;

    // Name , info
    HashMap<String, VarInfo> parameters;
    HashMap<String, VarInfo> locals;

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